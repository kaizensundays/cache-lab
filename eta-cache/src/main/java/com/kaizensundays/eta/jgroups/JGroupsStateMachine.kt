package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.Conditions
import com.kaizensundays.eta.raft.NodeEventListener
import com.kaizensundays.eta.raft.RaftStateMachine
import com.kaizensundays.eta.raft.RoleChangeListener
import com.kaizensundays.eta.raft.StateEventListener
import com.kaizensundays.eta.raft.Value
import org.jgroups.JChannel
import org.jgroups.raft.RaftHandle
import org.jgroups.raft.StateMachine
import org.jgroups.util.Bits
import org.jgroups.util.ByteArrayDataInputStream
import org.jgroups.util.ByteArrayDataOutputStream
import org.jgroups.util.Util
import java.io.DataInput
import java.io.DataOutput
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 10/13/2024, 12:46 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsStateMachine<K, V>(private val ch: JChannel, raftHandle: RaftHandle? = null) : RaftStateMachine<K, V>, StateMachine {

    companion object {
        const val PUT: Byte = 1
        const val REMOVE: Byte = 2
        const val GET: Byte = 3
        const val REMOVE_IF: Byte = 4
    }

    private val raft: RaftHandle = raftHandle ?: RaftHandle(ch, this)

    private val listenerMap = ConcurrentHashMap<StateEventListener<K, V?>, JGroupsStateEntryListener<K, V?>>()
    private val roleChangeListenerMap = ConcurrentHashMap<RoleChangeListener, JGroupsRoleChangeListener>()

    internal val map: ConcurrentMap<K, V> = ConcurrentHashMap()

    // timeout (ms) to wait for a majority to ack a write
    private var replicationTimeout: Long = 20000

    // If true, reads are served locally without going through RAFT.
    private var allowDirtyReads: Boolean = false
    private var classLoader: ClassLoader? = null

    fun allowDirtyReads(f: Boolean): RaftStateMachine<K, V> {
        this.allowDirtyReads = f
        return this
    }

    fun raftId(id: String?): RaftStateMachine<K, V> {
        raft.raftId(id)
        return this
    }

    fun useClassLoader(classLoader: ClassLoader?): RaftStateMachine<K, V> {
        this.classLoader = classLoader
        return this
    }

    /**
     * Adds a key value pair to the state machine. The data is not added directly, but sent to the RAFT leader and only
     * added to the hashmap after the change has been committed (by majority decision). The actual change will be
     * applied with callback [StateMachine.apply].
     *
     * @param key The key to be added.
     * @param val The value to be added
     * @return Null, or the previous value associated with key (if present)
     */
    @Throws(Exception::class)
    override fun put(key: K, value: V?): V? {
        return invoke(PUT, key, value, false)
    }

    /**
     * Returns the value for a given key.
     *
     *
     * When [.allow_dirty_reads] is set, the local value is returned, possibly stale and violating
     * linearizability. Otherwise, the request is sent through RAFT and returns a consistent value.
     *
     * @param key The key
     * @return The value associated with key (staleness configurable via [.allow_dirty_reads])
     */
    @Throws(Exception::class)
    override fun get(key: K): V? {
        if (allowDirtyReads) {
            synchronized(map) {
                return map[key]
            }
        }

        return invoke(GET, key, null, false)
    }

    /**
     * Removes a key-value pair from the state machine. The data is not removed directly from the hashmap, but an
     * update is sent via RAFT and the actual removal from the hashmap is only done when that change has been committed.
     *
     * @param key The key to be removed
     */
    @Throws(Exception::class)
    override fun remove(key: K): V? {
        return invoke(REMOVE, key, null, true)
    }

    override fun removeIf(key: K, condition: Conditions, params: List<Any>): V? {
        return invoke(REMOVE_IF, key, null, true, listOf(condition.ordinal) + params)
    }

    override fun getEntries(): MutableSet<MutableMap.MutableEntry<K, V>> {
        return map.entries
    }

    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        return map.iterator()
    }

    /** Returns the number of elements in the RSM  */
    fun size(): Int {
        synchronized(map) {
            return map.size
        }
    }

    private fun applyPut(ins: ByteArrayDataInputStream, serializeResponse: Boolean): ByteArray? {
        val key = Util.objectFromStream<K>(ins, classLoader)
        val value = Util.objectFromStream<V>(ins, classLoader)
        var oldValue: V?
        synchronized(map) {
            oldValue = map.put(key, value)
        }
        notifyPut(key, value, oldValue)
        return if (oldValue == null) null else (if (serializeResponse) Util.objectToByteBuffer(oldValue) else null)
    }


    private fun applyRemove(ins: ByteArrayDataInputStream, serializeResponse: Boolean): ByteArray? {
        val key = Util.objectFromStream<K>(ins, classLoader)
        var oldValue: V?
        synchronized(map) {
            oldValue = map.remove(key)
        }
        notifyRemove(key, oldValue)
        return if (oldValue == null) null else (if (serializeResponse) Util.objectToByteBuffer(oldValue) else null)
    }

    fun applyRemoveIf(ins: ByteArrayDataInputStream, serializeResponse: Boolean): ByteArray? {
        try {
            val key = Util.objectFromStream<K>(ins, classLoader)
            val conditionOrdinal = Util.objectFromStream<Int>(ins, classLoader)
            val condition = Conditions.fromOrdinal(conditionOrdinal)
            val param = Util.objectFromStream<Long>(ins, classLoader)
            val before = param
            var oldValue: V? = null
            var removed = false
            synchronized(map) {
                val entry = map[key]
                if (entry is Value<*>) {
                    if (condition.apply(entry, arrayOf(before))) {
                        oldValue = map.remove(key)
                        removed = true
                    }
                }
            }
            if (removed) {
                notifyRemove(key, oldValue)
            }
            return if (oldValue == null) null else (if (serializeResponse) Util.objectToByteBuffer(oldValue) else null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun applyGet(ins: ByteArrayDataInputStream, serializeResponse: Boolean): ByteArray? {
        val key = Util.objectFromStream<K>(ins, classLoader)
        var value: V?
        synchronized(map) {
            value = map[key]
        }
        notifyGet(key, value)
        return if (value == null) null else (if (serializeResponse) Util.objectToByteBuffer(value) else null)!!
    }

    @Throws(Exception::class)
    override fun apply(data: ByteArray, offset: Int, length: Int, serializeResponse: Boolean): ByteArray? {
        val ins = ByteArrayDataInputStream(data, offset, length)
        val command = ins.readByte()
        when (command) {
            PUT -> {
                return applyPut(ins, serializeResponse)
            }

            REMOVE -> {
                return applyRemove(ins, serializeResponse)
            }

            REMOVE_IF -> {
                return applyRemoveIf(ins, serializeResponse)
            }

            GET -> {
                return applyGet(ins, serializeResponse)
            }

            else -> throw IllegalArgumentException("command $command is unknown")
        }
    }

    @Throws(Exception::class)
    override fun readContentFrom(ins: DataInput) {
        val size = Bits.readIntCompressed(ins)
        val tmp: MutableMap<K, V> = HashMap(size)
        for (i in 0 until size) {
            val key = Util.objectFromStream<K>(ins, classLoader)
            val value = Util.objectFromStream<V>(ins, classLoader)
            tmp[key] = value
        }
        synchronized(map) {
            map.clear()
            map.putAll(tmp)
        }
    }

    @Throws(Exception::class)
    override fun writeContentTo(out: DataOutput) {
        synchronized(map) {
            val size = map.size
            Bits.writeIntCompressed(size, out)
            for ((key, value) in map) {
                Util.objectToStream(key, out)
                Util.objectToStream(value, out)
            }
        }
    }

    @Throws(Exception::class)
    protected fun invoke(command: Byte, key: K, value: V?, ignoreReturnValue: Boolean, params: List<Any> = emptyList()): V? {
        val out = ByteArrayDataOutputStream(256)
        try {
            out.writeByte(command.toInt())
            Util.objectToStream(key, out)
            if (value != null) Util.objectToStream(value, out)
            params.forEach { param ->
                Util.objectToStream(param, out)
            }
        } catch (ex: Exception) {
            throw Exception("serialization failure (key=$key, val=$value)", ex)
        }

        val buf = out.buffer()
        val rsp = raft.set(buf, 0, out.position(), replicationTimeout, TimeUnit.MILLISECONDS)
        return if (ignoreReturnValue || rsp == null) null else Util.objectFromByteBuffer(rsp, 0, rsp.size, classLoader)
    }

    private fun notifyPut(key: K, value: V, oldValue: V?) {
        for (listener in listenerMap.values) {
            try {
                listener.put(key, value, oldValue)
            } catch (ignored: Throwable) {
                //
            }
        }
    }

    private fun notifyRemove(key: K, oldValue: V?) {
        for (listener in listenerMap.values) {
            try {
                listener.remove(key, oldValue)
            } catch (ignored: Throwable) {
                //
            }
        }
    }

    private fun notifyGet(key: K, value: V?) {
        for (listener in listenerMap.values) {
            try {
                listener.get(key, value)
            } catch (ignored: Throwable) {
                //
            }
        }
    }

    override fun connect() {
        try {
            ch.connect("ignored")
        } catch (e: Exception) {
            ch.disconnect()
            throw IllegalStateException(e)
        }
    }

    override fun destroy() {

        // disconnect, releases all resources and destroys the channel
        ch.close()

        roleChangeListenerMap.clear()
        listenerMap.clear()
    }

    override fun withRaftId(id: String): RaftStateMachine<K, V> {
        return this.raftId(id)
    }

    override fun withReplicationTimeout(timeout: Long): RaftStateMachine<K, V> {
        replicationTimeout = timeout
        return this
    }

    override fun addEventListener(listener: NodeEventListener) {
        val eventListener = JGroupsEventListener(listener)
        this.ch.setReceiver(eventListener)
    }

    override fun addRoleChangeListener(listener: RoleChangeListener) {
        roleChangeListenerMap.computeIfAbsent(listener) { _ ->
            JGroupsRoleChangeListener(listener)
        }.let { l ->
            raft.addRoleListener(l)
        }
    }

    override fun removeRoleChangeListener(listener: RoleChangeListener) {
        roleChangeListenerMap.remove(listener)?.let { l ->
            raft.removeRoleListener(l)
        }
    }

    override fun addStateEventListener(listener: StateEventListener<K, V?>) {
        listenerMap.computeIfAbsent(listener) { _ ->
            JGroupsStateEntryListener(listener)
        }
    }

    override fun removeStateEventListener(listener: StateEventListener<K, V?>) {
        listenerMap.remove(listener)
    }

}
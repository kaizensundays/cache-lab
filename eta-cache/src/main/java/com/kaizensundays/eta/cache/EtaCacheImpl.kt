package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.Conditions
import com.kaizensundays.eta.raft.NodeEventType
import com.kaizensundays.eta.raft.RaftNode
import com.kaizensundays.eta.raft.RaftStateMachine
import com.kaizensundays.eta.raft.RaftStateMachineConfiguration
import com.kaizensundays.eta.raft.RaftStateMachineType
import com.kaizensundays.eta.raft.StateEvent
import com.kaizensundays.eta.raft.StateEventListener
import com.kaizensundays.eta.raft.StateEventType
import com.kaizensundays.eta.raft.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.cache.configuration.CacheEntryListenerConfiguration
import javax.cache.event.EventType
import javax.cache.expiry.ExpiryPolicy

/**
 * Created: Saturday 10/19/2024, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EtaCacheImpl<K, V>(
    private val conf: EtaNodeConfiguration,
    private val cacheConf: EtaCacheConfiguration<K, V>,
    private val raftNode: RaftNode
) : EtaCacheAdapter<K, V?>() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    var timeoutMs = 3000L

    private lateinit var rsm: RaftStateMachine<K, Value<V?>>

    private val expiryPolicy: ExpiryPolicy? = cacheConf.expiryPolicyFactory.create()

    private var expiryScheduler: ExpiryScheduler = ExpirySchedulerImpl(this)

    override fun getName(): String {
        return cacheConf.cacheName
    }

    override fun get(key: K): V? {
        logger.info(">>> get: {}", key)
        val entry = rsm.get(key)
        return entry?.value
    }

    override fun put(key: K, value: V?) {
        if (value != null) {
            rsm.put(key, Value(value, System.currentTimeMillis()))
        }
    }

    override fun remove(key: K): Boolean {
        return rsm.remove(key) != null
    }

    private fun <K, V> MutableIterator<Map.Entry<K, Value<V>>>.toCacheEntryIterator(): MutableIterator<CacheEntry<K, V>> {

        return object : MutableIterator<CacheEntry<K, V>> {
            override fun hasNext(): Boolean {
                return this@toCacheEntryIterator.hasNext()
            }

            override fun next(): CacheEntry<K, V> {
                val entry = this@toCacheEntryIterator.next()
                return CacheEntry(entry.key, entry.value.value)
            }

            override fun remove() {
                this@toCacheEntryIterator.remove()
            }
        }
    }

    override fun iterator(): MutableIterator<CacheEntry<K, V?>> {

        return rsm.iterator().toCacheEntryIterator()
    }

    override fun connect() {
        try {
            rsm.connect()
        } catch (e: Exception) {
            logger.error("", e)
            throw IllegalStateException(e)
        }
    }

    private fun touch(event: StateEvent<K, Value<V?>?>) {
        val touched = System.currentTimeMillis()
        logger.info("touched=$touched")
        event.value?.let { value -> value.touched = touched }
    }

    override fun removeExpired() {
        if (expiryPolicy != null) {
            val duration = expiryPolicy.expiryForAccess
            val durationMs = duration.timeUnit.toMillis(duration.durationAmount)
            logger.info("durationMs=$durationMs")
            val before = System.currentTimeMillis() - durationMs

            rsm.getEntries().removeIf { (key, valueObj) ->
                return@removeIf if (valueObj.touched < before) {
                    logger.info("removeIf - before=${Date(before)}")
                    rsm.removeIf(key, Conditions.IF_TOUCHED_BEFORE, listOf(before))
                    true
                } else {
                    false
                }
            }

        }
    }

    override fun registerCacheEntryListener(configuration: CacheEntryListenerConfiguration<K, V?>) {

        val cache = this

        val ref = configuration.cacheEntryListenerFactory.create()

        val listener = CacheEntryListenerAdapter(ref)

        val stateEventListener = StateEventListener<K, Value<V?>?> { event ->
            when (event.type) {
                StateEventType.Put -> {
                    val entryEvent = CacheEntryEventIml(cache, EventType.CREATED, event.key, event.value?.value, event.oldValue?.value)
                    listener.onUpdated(mutableListOf(entryEvent))
                }

                StateEventType.Remove -> {
                    val entryEvent = CacheEntryEventIml(cache, EventType.REMOVED, event.key, event.value?.value, event.oldValue?.value)
                    listener.onUpdated(mutableListOf(entryEvent))
                }

                else -> {
                    //
                }
            }
        }
        rsm.addStateEventListener(stateEventListener)
    }

    override fun deregisterCacheEntryListener(cacheEntryListenerConfiguration: CacheEntryListenerConfiguration<K, V?>) {
        //
    }

    override fun init() {

        rsm = raftNode.create(
            RaftStateMachineConfiguration(
                RaftStateMachineType.DEFAULT,
                cacheConf.cacheName,
                conf.nodeName,
                conf.members,
                cacheConf.logType,
                conf.logDir
            )
        )

        rsm.withRaftId(conf.nodeName).withReplicationTimeout(timeoutMs)

        rsm.addEventListener { type, msg ->
            when (type) {
                NodeEventType.ViewAccepted -> {
                    logger.info("cacheName: {} view change: {}", cacheConf.cacheName, msg)
                }
            }
        }

        rsm.addRoleChangeListener { role ->
            logger.info("-- changed role to $role")
            if ("leader" == role.lowercase()) {
                logger.info("Starting evictor")
                expiryScheduler.start()
            } else {
                logger.info("Stopping evictor")
                expiryScheduler.stop()
            }
        }

        rsm.addStateEventListener { event ->
            when (event.type) {
                StateEventType.Put -> {
                    //logger.info("-- put({}, {}) -> {}\n", event.key, event.value?.value, event.oldValue?.value)
                    event.key?.let { touch(event) }
                }

                StateEventType.Get -> {
                    logger.info("-- get({}) -> {}\n", event.key, event.value?.value)
                    event.key?.let { touch(event) }
                }

                StateEventType.Remove -> {
                    logger.info("-- remove({}) -> {}\n", event.key, event.oldValue?.value)
                }
            }
        }

    }

    override fun destroy() {
        expiryScheduler.stop()
        rsm.destroy()
    }

}
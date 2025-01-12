package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.MessageBus
import com.kaizensundays.eta.raft.MessageListener
import org.jgroups.JChannel
import org.jgroups.Message
import org.jgroups.Receiver
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created: Sunday 12/22/2024, 11:23 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsMessageBus(private val channel: JChannel) : MessageBus, Receiver {

    private val listenerMap = ConcurrentHashMap<String, MutableList<MessageListener>>()

    fun getListeners(topic: String): List<MessageListener> {
        return listenerMap[topic]?.toList() ?: emptyList()
    }

    override fun addListener(topic: String, listener: MessageListener) {
        listenerMap.computeIfAbsent(topic) { CopyOnWriteArrayList() }.apply {
            if (!contains(listener)) {
                add(listener)
            }
        }
    }

    override fun removeListener(topic: String, listener: MessageListener) {
        listenerMap.compute(topic) { _, listeners ->
            listeners?.apply { remove(listener) }
            if (listeners.isNullOrEmpty()) null else listeners
        }
    }

    private fun marshal(msg: JGroupsMessage): ByteArray {
        ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { oos ->
                oos.writeObject(msg)
            }
            return bos.toByteArray()
        }
    }

    private fun unmarshal(data: ByteArray): JGroupsMessage {
        ByteArrayInputStream(data).use { bis ->
            ObjectInputStream(bis).use { ois ->
                return ois.readObject() as JGroupsMessage
            }
        }
    }

    override fun receive(msg: Message) {
        if (msg.hasArray()) {
            val data = msg.array
            val m = unmarshal(data)
            listenerMap.compute(m.topic) { _, listeners ->
                listeners?.forEach { listener -> listener.onMessage(m.msg) }
                listeners
            }
        }
    }

    override fun send(topic: String, message: ByteArray) {

        val bytes = marshal(JGroupsMessage(topic, message))

        check(channel.isOpen)

        channel.send(null, bytes)
    }

}
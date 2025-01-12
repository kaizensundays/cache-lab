package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.Notification
import com.kaizensundays.eta.raft.StateEvent
import com.kaizensundays.eta.raft.StateEventListener
import com.kaizensundays.eta.raft.StateEventType
import javax.cache.event.CacheEntryListener

/**
 * Created: Sunday 12/8/2024, 8:31 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsStateEntryListener<K, V>(private val listener: StateEventListener<K, V>) : CacheEntryListener<K, V>, Notification<K, V> {

    override fun put(key: K, value: V, oldValue: V?) {
        listener.onEvent(StateEvent(StateEventType.Put, key, value, oldValue))
    }

    override fun get(key: K, value: V?) {
        listener.onEvent(StateEvent(StateEventType.Get, key, value, null))
    }

    override fun remove(key: K, oldValue: V?) {
        listener.onEvent(StateEvent(StateEventType.Remove, key, null, oldValue))
    }

}
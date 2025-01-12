package com.kaizensundays.eta.raft

/**
 * Created: Wednesday 12/25/2024, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class StateEvent<K, V>(
    val type: StateEventType,
    val key: K,
    val value: V? = null,
    val oldValue: V? = null
) : Event
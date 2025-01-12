package com.kaizensundays.kappa.raft

/**
 * Created: Wednesday 12/25/2024, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class StateEvent<K, V>(
    val type: SateEventType,
    val key: K,
    val value: V? = null,
    val oldValue: V? = null
) : Event
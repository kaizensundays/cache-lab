package com.kaizensundays.kappa.raft

/**
 * Created: Wednesday 12/25/2024, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheEvent<K, V>(
    val type: CacheEventType,
    val key: K? = null,
    val value: V? = null,
    val oldValue: V? = null,
    val name: String? = null
) : Event {

    constructor(type: CacheEventType, name: String) : this(type, null, null, null, name)

}

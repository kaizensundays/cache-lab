package com.kaizensundays.kappa.raft

/**
 * Created: Saturday 10/12/2024, 8:47 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface RaftStateMachine<K, V> {

    fun connect()

    fun destroy()

    fun withRaftId(id: String): RaftStateMachine<K, V>

    fun withReplicationTimeout(timeout: Long): RaftStateMachine<K, V>

    fun addEventListener(listener: NodeEventListener)

    fun addRoleChangeListener(listener: RoleChangeListener)

    fun removeRoleChangeListener(listener: RoleChangeListener)

    fun addStateEventListener(listener: StateEventListener<K, V?>)

    fun removeStateEventListener(listener: StateEventListener<K, V?>)

    fun get(key: K): V?

    fun put(key: K, value: V?): V?

    fun remove(key: K): V?

    fun removeIf(key: K, condition: Conditions, params: List<Any>): V?

    fun getEntries(): MutableSet<MutableMap.MutableEntry<K, V>>

    fun iterator(): MutableIterator<Map.Entry<K, V>>

}
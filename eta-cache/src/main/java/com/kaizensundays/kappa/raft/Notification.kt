package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/22/2024, 6:01 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface Notification<K, V> {

    fun put(key: K, value: V, oldValue: V?)

    fun remove(key: K, oldValue: V?)

    fun get(key: K, value: V?)

}
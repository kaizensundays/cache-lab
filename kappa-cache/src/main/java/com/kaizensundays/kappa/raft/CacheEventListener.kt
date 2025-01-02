package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/8/2024, 8:34 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface CacheEventListener<K, V> {

    fun onEvent(event: CacheEvent<K, V>)

}
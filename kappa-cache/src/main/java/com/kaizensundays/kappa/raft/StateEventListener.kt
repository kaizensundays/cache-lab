package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/8/2024, 8:34 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface StateEventListener<K, V> {

    fun onEvent(event: StateEvent<K, V>)

}
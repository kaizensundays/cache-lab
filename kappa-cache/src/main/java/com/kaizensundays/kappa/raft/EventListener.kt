package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/8/2024, 3:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface EventListener {

    fun onEvent(type: EventType, msg: String)

}
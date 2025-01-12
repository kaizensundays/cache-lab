package com.kaizensundays.eta.raft

/**
 * Created: Sunday 12/8/2024, 3:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface NodeEventListener {

    fun onEvent(type: NodeEventType, msg: String)

}
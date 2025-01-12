package com.kaizensundays.eta.raft

/**
 * Created: Sunday 12/22/2024, 12:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NopNodeEventListener : NodeEventListener {

    override fun onEvent(type: NodeEventType, msg: String) {
        //
    }

}
package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/22/2024, 12:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NopEventListener : EventListener {

    override fun onEvent(type: EventType, msg: String) {
        //
    }

}
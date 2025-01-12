package com.kaizensundays.eta.raft

/**
 * Created: Sunday 12/29/2024, 5:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface RoleChangeListener {

    fun onEvent(role: String)

}
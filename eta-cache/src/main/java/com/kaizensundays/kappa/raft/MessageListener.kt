package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/22/2024, 11:20 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface MessageListener {

    fun onMessage(msg: ByteArray)

}
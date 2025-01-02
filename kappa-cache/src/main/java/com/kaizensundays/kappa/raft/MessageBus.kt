package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/22/2024, 12:18 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface MessageBus {

    fun addListener(topic: String, listener: MessageListener)

    fun removeListener(topic: String, listener: MessageListener)

    fun send(topic: String, message: ByteArray)

}
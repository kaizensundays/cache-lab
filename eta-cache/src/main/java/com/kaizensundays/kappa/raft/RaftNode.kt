package com.kaizensundays.kappa.raft

/**
 * Created: Saturday 12/14/2024, 12:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface RaftNode {

    fun configure(configuration: RaftNodeConfiguration)

    fun init()

    fun connect()

    fun destroy()

    fun <K, V> create(conf: RaftStateMachineConfiguration): RaftStateMachine<K, Value<V?>>

    fun getMessageBus(): MessageBus

}
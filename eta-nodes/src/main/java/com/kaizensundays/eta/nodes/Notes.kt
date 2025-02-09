package com.kaizensundays.eta.nodes

/**
 * Created: Sunday 2/9/2025, 11:20 AM Eastern Time
 *
 * @author Sergey Chuykov
 */

interface Producer

class OkHttpProducer : Producer

interface Initiator {
    fun connect()
    fun disconnect()
}

// Session
class SessionInitiator(producer: Producer) : Initiator {
    override fun connect() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}

interface Acceptor

class WebFluxAcceptor : Acceptor

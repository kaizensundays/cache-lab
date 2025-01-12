package com.kaizensundays.eta.jgroups

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

/**
 * Created: Saturday 9/30/2023, 7:28 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class AbstractWebSocketHandler : WebSocketHandler {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val outbound = Sinks.many().multicast().directBestEffort<ByteArray>()

    abstract fun handle(msg: ByteArray, outbound: Sinks.Many<ByteArray>)

    private fun handle(message: WebSocketMessage) {
        val data = message.payload
        val msg = ByteArray(data.readableByteCount())
        data.read(msg)
        handle(msg, outbound)
    }

    override fun handle(session: WebSocketSession): Mono<Void> {

        val sub = session.receive()
            .map { message -> handle(message) }
            .then()

        val pub = session.send(
            outbound.asFlux().map { msg -> session.binaryMessage { factory -> factory.wrap(msg) } }
        )

        return Mono.zip(sub, pub).then()
    }
}

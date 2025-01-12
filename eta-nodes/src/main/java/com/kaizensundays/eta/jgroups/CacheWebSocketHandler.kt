package com.kaizensundays.eta.jgroups

import reactor.core.publisher.Sinks

/**
 * Created: Saturday 1/27/2024, 5:04 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheWebSocketHandler(private val handler: CacheCommandHandler) : AbstractWebSocketHandler() {

    override fun handle(msg: ByteArray, outbound: Sinks.Many<ByteArray>) {
        logger.debug("msg={}", String(msg))

        val result = handler.execute(String(msg))

        outbound.tryEmitNext(result.toByteArray())
    }

}
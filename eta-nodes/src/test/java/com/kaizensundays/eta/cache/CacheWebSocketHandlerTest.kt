package com.kaizensundays.eta.cache

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import reactor.core.publisher.Sinks

/**
 * Created: Sunday 10/6/2024, 1:48 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheWebSocketHandlerTest {

    private val handler: CacheCommandHandler = mock()
    private val sink: Sinks.Many<ByteArray> = mock()
    private val webSocketHandler = CacheWebSocketHandler(handler)

    @Test
    fun pingPong() {

        val ping = "ping"
        val pong = "pong"

        whenever(handler.execute(ping)).thenReturn(pong)
        whenever(sink.tryEmitNext(any())).thenReturn(Sinks.EmitResult.OK)

        webSocketHandler.handle(ping.toByteArray(), sink)

        verify(handler).execute(ping)
        verify(sink).tryEmitNext(pong.toByteArray())
        verifyNoMoreInteractions(handler)
        verifyNoMoreInteractions(sink)
    }

}
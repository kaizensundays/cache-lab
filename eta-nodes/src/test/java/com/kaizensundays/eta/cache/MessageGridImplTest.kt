package com.kaizensundays.eta.cache

import com.kaizensundays.eta.context.TestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

/**
 * Created: Saturday 12/21/2024, 11:11 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MessageGridImplTest {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val clusterName = this.javaClass.simpleName

    private val grid = TestContext().createMessageGrid(clusterName)

    @Test
    fun test() {

        val n = 4

        val result = grid.toFlux("?")
            .take(n.toLong())
            .doOnSubscribe { _ ->
                Flux.range(0, n)
                    .delaySequence(Duration.ofSeconds(1))
                    .doOnNext { n -> grid.send("?", "Ping$n") }
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
            .collectList()
            .block(Duration.ofSeconds(10))

        assertNotNull(result)
        assertEquals(n, result.size)
    }

}
package com.kaizensundays.eta.cache

import com.kaizensundays.messaging.WebFluxProducer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.lang.Thread.sleep
import java.time.Duration

/**
 * Created: Sunday 11/17/2024, 12:35 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheContainerTest : CacheContainerTestSupport() {

    private lateinit var producers: List<WebFluxProducer>

    @BeforeEach
    fun before() {
        producers = producers()
    }

    @Test
    fun expiration() {
        sleep(3_000)

        var result = producers[0].request("get:/ping")
        logger.info("{}", result)

        result = producers[0].request("get:/put/a/A")
        assertTrue(result.isBlank())

        result = producers[1].request("get:/get/a")
        logger.info("{}", result)
        Assertions.assertEquals("A", result)

        sleep(20_000)

        result = producers[1].request("get:/get/a")
        logger.info("{}", result)
        assertTrue(result.isBlank())

        sleep(3_000)
    }

    @Test
    fun messageRounds() {
        sleep(3_000)

        val driver = TestDriver(producers[0])

        val keyNum = 10
        val messagesPerKey = 2
        val rounds = 8
        val total = keyNum * messagesPerKey * rounds

        val f = driver.execute(keyNum, messagesPerKey, rounds)

        val done = StepVerifier.create(f)
            .expectNextCount(total.toLong())
            .verifyComplete()

        assertTrue(done < Duration.ofSeconds(100))

        sleep(3000)
    }

}
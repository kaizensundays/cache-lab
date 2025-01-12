package com.kaizensundays.eta.jgroups

import com.kaizensundays.messaging.DefaultLoadBalancer
import com.kaizensundays.messaging.Instance
import com.kaizensundays.messaging.LoadBalancer
import com.kaizensundays.messaging.WebFluxProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.URI
import java.time.Duration
import kotlin.test.assertTrue

/**
 * Created: Sunday 10/6/2024, 1:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Suppress("MemberVisibilityCanBePrivate")
@ActiveProfiles("test")
@ContextConfiguration(locations = ["/CacheIntegrationTest.xml"])
class CacheIntegrationTest : IntegrationTestSupport() {

    private lateinit var loadBalancer: LoadBalancer

    private lateinit var producer: WebFluxProducer

    @LocalServerPort
    var embeddedPort = 0
    val remotePort = 7701
    val remote = false

    @BeforeEach
    fun before() {
        val port = if (remote) remotePort else embeddedPort
        loadBalancer = DefaultLoadBalancer(listOf(Instance("localhost", port)))
        producer = WebFluxProducer(loadBalancer)
        logger.info("java.net.preferIPv4Stack={}", System.getProperty("java.net.preferIPv4Stack"))
    }

    @Test
    fun send() {

        val msg = "test".toByteArray()

        val topic = URI("ws:/default/ws?maxAttempts=3")

        val m = producer.send(topic, msg)

        val done = StepVerifier.create(m)
            .verifyComplete()

        assertTrue(done < Duration.ofSeconds(10))
    }

    @Test
    fun ping() {

        val msg = "ping".toByteArray()

        val topic = URI("ws:/default/ws?maxAttempts=3")

        val resp = producer.request(topic, msg)
            .take(1)
            .map { String(it) }
            .collectList()
            .switchIfEmpty(Mono.just(emptyList()))
            .block(Duration.ofSeconds(10))

        assertNotNull(resp)
        assertEquals(listOf("Ok"), resp)
    }

    @Test
    fun cache() {

        val driver = TestDriver(producer)

        val keyNum = 10
        val messagesPerKey = 2
        val rounds = 1
        val total = keyNum * messagesPerKey * rounds

        val f = driver.execute(keyNum, messagesPerKey, rounds)

        val done = StepVerifier.create(f)
            .expectNextCount(total.toLong())
            .verifyComplete()

        assertTrue(done < Duration.ofSeconds(1000))
    }

}
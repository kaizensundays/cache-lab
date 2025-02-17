package com.kaizensundays.eta.cache

import com.kaizensundays.messaging.WebFluxProducer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

/**
 * Created: Sunday 2/9/2025, 12:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheRemoteTest : CacheContainerTestSupport() {

    private lateinit var producers: List<WebFluxProducer>

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            println("")
        }
    }

    @BeforeEach
    fun before() {
        producers = producers()
    }

    @Test
    fun test() {
        sleep(300_000)

    }

}
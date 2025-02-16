package com.kaizensundays.eta.cache

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

/**
 * Created: Sunday 2/16/2025, 12:19 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheCommandHandlerTest {

    private val cache: EtaCache<String, String> = mock()

    private val handler = CacheCommandHandler(cache)

    @Test
    fun put() {

        handler.execute(CachePut("a", "A"))

        verify(cache).put("a", "A")
    }

    @Test
    fun get() {

        whenever(cache.get("a")).thenReturn("A")

        val res = handler.execute(CacheGet("a"))
        assertTrue(res is CacheValue)
        assertEquals("A", res.value)

        verify(cache).get("a")
    }

}
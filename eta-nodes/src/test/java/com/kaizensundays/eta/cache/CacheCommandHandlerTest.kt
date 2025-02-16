package com.kaizensundays.eta.cache

import com.kaizensundays.eta.jgroups.CacheCommandHandler
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

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

        handler.execute(CacheGet("a"))

        verify(cache).get("a")
    }

}
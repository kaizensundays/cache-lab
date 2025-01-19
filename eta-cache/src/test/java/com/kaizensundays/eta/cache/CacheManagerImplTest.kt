package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.LogType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import javax.cache.Caching


/**
 * Created: Sunday 1/5/2025, 5:25 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheManagerImplTest {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun createCache() {

        val provider = Caching.getCachingProvider()
        assertNotNull(provider)

        val manager = provider.getCacheManager(null, null)
        assertNotNull(manager)

        val conf = EtaCacheConfiguration<String, String>()
        conf.logType = LogType.InMemoryLog

        val cache = manager.createCache("default", conf)

        cache.registerCacheEntryListener(
            LoggingCacheEntryListener<String, String>()
                .configuration(isOldValueRequired = false, isSynchronous = false)
        )

        sleep(3000)

        cache.put("a", "A")
        cache.put("b", "B")
        cache.put("c", "C")

        val v = cache.get("a")
        logger.info("v={}", v)

        sleep(3000)
    }

}
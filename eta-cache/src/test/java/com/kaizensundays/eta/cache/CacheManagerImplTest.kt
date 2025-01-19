package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.LogType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import javax.cache.Caching


/**
 * Created: Sunday 1/5/2025, 5:25 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheManagerImplTest {

    //private val manager = CacheManagerImpl()

    @Test
    fun createCache() {

        val provider = Caching.getCachingProvider()
        assertNotNull(provider)

        val manager = provider.getCacheManager(null, null)
        assertNotNull(manager)

        val conf = EtaCacheConfiguration<String, String>()
        conf.logType = LogType.InMemoryLog

        val cache = manager.createCache("default", conf)

        sleep(3000)
    }

}
package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.RaftNode
import java.net.URI
import java.util.*
import javax.cache.CacheManager
import javax.cache.configuration.OptionalFeature

/**
 * Created: Sunday 1/5/2025, 6:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CachingProvider : javax.cache.spi.CachingProvider {
    override fun close() {
        TODO("Not yet implemented")
    }

    override fun close(classLoader: ClassLoader?) {
        TODO("Not yet implemented")
    }

    override fun close(uri: URI?, classLoader: ClassLoader?) {
        TODO("Not yet implemented")
    }

    override fun getCacheManager(uri: URI?, classLoader: ClassLoader?, properties: Properties?): CacheManager {
        TODO("Not yet implemented")
    }

    override fun getCacheManager(uri: URI?, classLoader: ClassLoader?): CacheManager {
        TODO("Not yet implemented")
    }

    override fun getCacheManager(): CacheManager {
        TODO("Not yet implemented")
    }

    override fun getDefaultClassLoader(): ClassLoader {
        TODO("Not yet implemented")
    }

    override fun getDefaultURI(): URI {
        TODO("Not yet implemented")
    }

    override fun getDefaultProperties(): Properties {
        TODO("Not yet implemented")
    }

    override fun isSupported(optionalFeature: OptionalFeature?): Boolean {
        TODO("Not yet implemented")
    }

    fun createNode(): RaftNode {

        val loader = ServiceLoader.load(RaftNode::class.java)

        val node = loader.findFirst()

        if (node.isPresent) {
            return node.get()
        } else {
            error("")
        }
    }
}
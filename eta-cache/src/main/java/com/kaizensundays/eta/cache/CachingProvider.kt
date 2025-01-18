package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.RaftNode
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicReference
import javax.cache.CacheManager
import javax.cache.configuration.OptionalFeature

/**
 * Created: Sunday 1/5/2025, 6:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CachingProvider : javax.cache.spi.CachingProvider {

    private val defaultURI = URI.create("eta://default")

    private val cacheManagers: MutableMap<ClassLoader, ConcurrentMap<URI, AtomicReference<CacheManager>>> = WeakHashMap()

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun close(classLoader: ClassLoader?) {
        TODO("Not yet implemented")
    }

    override fun close(uri: URI?, classLoader: ClassLoader?) {
        TODO("Not yet implemented")
    }

    override fun getCacheManager(uri: URI?, classLoader: ClassLoader?, properties: Properties): CacheManager? {

        val cfgUri = uri ?: getDefaultURI()

        val clsLdr = classLoader ?: defaultClassLoader

        var ref: AtomicReference<CacheManager>

        var needStartMgr = false

        synchronized(cacheManagers) {

            var cacheManagerMap = cacheManagers[clsLdr]

            if (cacheManagerMap == null) {
                cacheManagerMap = ConcurrentHashMap()
                cacheManagers[clsLdr] = cacheManagerMap
            }

            ref = cacheManagerMap.computeIfAbsent(cfgUri) { _ ->
                needStartMgr = true
                AtomicReference()
            }
        }

        if (needStartMgr) {

            val manager = CacheManagerImpl(cfgUri, this, clsLdr, properties)

            ref.set(manager)
        }

        return null
    }

    override fun getCacheManager(uri: URI?, classLoader: ClassLoader?): CacheManager? {
        return getCacheManager(uri, classLoader, Properties())
    }

    override fun getCacheManager(): CacheManager {
        TODO("Not yet implemented")
    }

    override fun getDefaultClassLoader(): ClassLoader {
        return javaClass.classLoader
    }

    override fun getDefaultURI(): URI {
        return defaultURI
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
package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.RaftNode
import com.kaizensundays.eta.raft.RaftNodeConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import java.util.concurrent.ConcurrentHashMap

/**
 * Created: Saturday 12/14/2024, 12:41 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EtaCacheNodeImpl(
    private val raftNode: RaftNode,
    private val configuration: EtaNodeConfiguration
) : EtaCacheNode, InitializingBean, DisposableBean {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val cacheMap: MutableMap<String, EtaCache<*, *>> = ConcurrentHashMap()

    override fun getNodeConfiguration(): EtaNodeConfiguration {
        return configuration
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V> getCache(cacheName: String): EtaCache<K, V> {
        return (cacheMap[cacheName] ?: throw IllegalStateException()) as EtaCache<K, V>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V> getOrCreateCache(conf: EtaNodeConfiguration, cacheConf: EtaCacheConfiguration<K, V>): EtaCache<K, V> {
        return cacheMap.computeIfAbsent(cacheConf.cacheName) { _ ->
            EtaCacheImpl(conf, cacheConf, raftNode)
        } as EtaCache<K, V>
    }

    override fun init() {

        raftNode.configure(
            RaftNodeConfiguration(
                configuration.useUDP,
                configuration.bindAddr,
                configuration.bindPort,
                configuration.logDir,
                configuration.nodeName,
                configuration.members
            )
        )

        raftNode.init()
        raftNode.connect()

        configuration.cacheConfiguration.forEach { cacheConf ->
            val cache = getOrCreateCache(configuration, cacheConf)
            cache.init()
            cache.connect()
        }

        logger.info("Stared")
    }

    override fun afterPropertiesSet() {
        init()
    }

    override fun destroy() {

        cacheMap.entries.forEach { e -> e.value.destroy() }

        raftNode.destroy()

        logger.info("Stopped")
    }

}
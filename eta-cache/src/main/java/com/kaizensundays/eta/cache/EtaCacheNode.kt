package com.kaizensundays.eta.cache

/**
 * Created: Sunday 10/20/2024, 1:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Suppress(
    "kotlin:S6517", // functional interface
)
interface EtaCacheNode {

    fun getNodeConfiguration(): EtaNodeConfiguration

    fun <K, V> getCache(cacheName: String): EtaCache<K, V>

    fun <K, V> getOrCreateCache(conf: EtaNodeConfiguration, cacheConf: EtaCacheConfiguration<K, V>): EtaCache<K, V>

    fun init()

    fun destroy()

}
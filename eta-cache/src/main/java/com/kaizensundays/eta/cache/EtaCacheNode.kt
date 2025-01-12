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

    fun <K, V> getCache(cacheName: String): EtaCache<K, V>

}
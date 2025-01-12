package com.kaizensundays.eta.cache

import javax.cache.configuration.Factory
import javax.cache.event.CacheEntryListener

/**
 * Created: Monday 1/6/2025, 7:14 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheEntryListenerFactory<K, V>(
    private val listener: CacheEntryListener<in K, in V>
) : Factory<CacheEntryListener<in K, in V>> {

    override fun create(): CacheEntryListener<in K, in V> {
        return listener
    }

}
package com.kaizensundays.eta.cache

import javax.cache.configuration.MutableCacheEntryListenerConfiguration
import javax.cache.event.CacheEntryListener

/**
 * Created: Monday 1/6/2025, 8:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class AbstractCacheEntryListener<K, V> : CacheEntryListener<K, V> {

    fun configuration(isOldValueRequired: Boolean, isSynchronous: Boolean): MutableCacheEntryListenerConfiguration<K, V> {

        val factory = CacheEntryListenerFactory(this)

        return MutableCacheEntryListenerConfiguration(factory, null, isOldValueRequired, isSynchronous)
    }

}
package com.kaizensundays.eta.cache

import javax.cache.Cache
import javax.cache.event.CacheEntryEvent
import javax.cache.event.EventType

/**
 * Created: Saturday 1/4/2025, 12:26 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheEntryEventIml<K, V>(
    cache: Cache<K, V>, eventType: EventType,
    private val key: K, private val value: V?, private val oldValue: V?
) : CacheEntryEvent<K, V>(cache, eventType) {

    override fun getKey(): K {
        return key
    }

    override fun getValue(): V? {
        return value
    }

    override fun <T : Any?> unwrap(clazz: Class<T>?): T? {
        return null
    }

    override fun getOldValue(): V? {
        return oldValue
    }

    override fun isOldValueAvailable(): Boolean {
        return false
    }
}
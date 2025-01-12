package com.kaizensundays.eta.cache

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.cache.event.CacheEntryCreatedListener
import javax.cache.event.CacheEntryEvent
import javax.cache.event.CacheEntryRemovedListener

/**
 * Created: Monday 1/6/2025, 7:24 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class LoggingCacheEntryListener<K, V> : AbstractCacheEntryListener<K, V>(),
    CacheEntryCreatedListener<K, V>, CacheEntryRemovedListener<K, V> {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun onCreated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        events.forEach { event ->
            logger.info("!!! put({}, {}) -> {}\n", event.key, event.value, event.oldValue)
        }
    }

    override fun onRemoved(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        events.forEach { event ->
            logger.info("!!! remove({}, {}) -> {}\n", event.key, event.value, event.oldValue)
        }
    }

}
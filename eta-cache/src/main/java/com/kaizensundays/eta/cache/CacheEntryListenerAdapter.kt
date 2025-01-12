package com.kaizensundays.eta.cache

import javax.cache.event.CacheEntryCreatedListener
import javax.cache.event.CacheEntryEvent
import javax.cache.event.CacheEntryExpiredListener
import javax.cache.event.CacheEntryListener
import javax.cache.event.CacheEntryRemovedListener
import javax.cache.event.CacheEntryUpdatedListener
import javax.cache.event.EventType

/**
 * Created: Thursday 1/2/2025, 8:30 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheEntryListenerAdapter<K, V>(private val listener: CacheEntryListener<K, V>) : CacheEntryUpdatedListener<K, V> {

    override fun onUpdated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {
        for (event in events) {
            when (event.eventType) {
                EventType.CREATED -> {
                    if (listener is CacheEntryCreatedListener) {
                        listener.onCreated(listOf(event))
                    }
                }

                EventType.UPDATED -> {
                    if (listener is CacheEntryUpdatedListener) {
                        listener.onUpdated(listOf(event))
                    }
                }

                EventType.REMOVED -> {
                    if (listener is CacheEntryRemovedListener) {
                        listener.onRemoved(listOf(event))
                    }
                }

                EventType.EXPIRED -> {
                    if (listener is CacheEntryExpiredListener) {
                        listener.onExpired(listOf(event))
                    }
                }

                else -> error("Unexpected EventType:" + event.eventType)
            }
        }
    }

}
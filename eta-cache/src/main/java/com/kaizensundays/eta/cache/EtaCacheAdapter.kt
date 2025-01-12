package com.kaizensundays.eta.cache

import javax.cache.CacheManager
import javax.cache.configuration.Configuration
import javax.cache.integration.CompletionListener
import javax.cache.processor.EntryProcessor
import javax.cache.processor.EntryProcessorResult

/**
 * Created: Saturday 10/5/2024, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class EtaCacheAdapter<K, V> : EtaCache<K, V> {

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun removeAll() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        return "?"
    }

    override fun getCacheManager(): CacheManager {
        TODO("Not yet implemented")
    }

    override fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> unwrap(clazz: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAll(keys: MutableSet<out K>?, entryProcessor: EntryProcessor<K, V, T>?, vararg arguments: Any?): MutableMap<K, EntryProcessorResult<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invoke(key: K, entryProcessor: EntryProcessor<K, V, T>?, vararg arguments: Any?): T {
        TODO("Not yet implemented")
    }

    override fun <C : Configuration<K, V>?> getConfiguration(clazz: Class<C>?): C {
        TODO("Not yet implemented")
    }

    override fun removeAll(keys: MutableSet<out K>?) {
        TODO("Not yet implemented")
    }

    override fun getAndReplace(key: K, value: V): V {
        TODO("Not yet implemented")
    }

    override fun replace(key: K, value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAndRemove(key: K): V {
        TODO("Not yet implemented")
    }

    override fun remove(key: K, oldValue: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun putIfAbsent(key: K, value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun putAll(map: MutableMap<out K, out V>?) {
        TODO("Not yet implemented")
    }

    override fun getAndPut(key: K, value: V): V {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override fun loadAll(keys: MutableSet<out K>?, replaceExistingValues: Boolean, completionListener: CompletionListener?) {
        TODO("Not yet implemented")
    }

    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAll(keys: MutableSet<out K>?): MutableMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V {
        TODO("Not yet implemented")
    }
}
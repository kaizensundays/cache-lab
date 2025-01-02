package com.kaizensundays.kappa.raft

import java.io.Serializable
import javax.cache.Cache

/**
 * Created: Saturday 11/30/2024, 12:09 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheEntry<K, V>(private val key: K, private val value: V, val touched: Long) : Cache.Entry<K, V>, Serializable {

    override fun getKey(): K {
        return key
    }

    override fun getValue(): V {
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> unwrap(clazz: Class<T>?): T {
        return this as T
    }

}
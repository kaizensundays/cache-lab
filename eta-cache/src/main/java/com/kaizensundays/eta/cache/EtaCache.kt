package com.kaizensundays.eta.cache

import javax.cache.Cache

/**
 * Created: Saturday 10/5/2024, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface EtaCache<K, V> : Cache<K, V> {

    fun init()

    fun connect()

    fun destroy()

    fun removeExpired()

}
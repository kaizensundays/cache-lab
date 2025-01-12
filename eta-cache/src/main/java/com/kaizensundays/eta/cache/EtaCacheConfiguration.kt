package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.LogType
import javax.cache.configuration.MutableConfiguration

/**
 * Created: Saturday 10/19/2024, 12:25 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EtaCacheConfiguration<K, V> : MutableConfiguration<K, V>() {

    var cacheName: String = ""
    var logType: LogType = LogType.InMemoryLog

}
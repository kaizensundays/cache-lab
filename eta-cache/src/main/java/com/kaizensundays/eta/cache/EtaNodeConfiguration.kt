package com.kaizensundays.eta.cache

/**
 * Created: Saturday 11/2/2024, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EtaNodeConfiguration {

    var useUDP = true
    var bindAddr: String = ""
    var bindPort = 0
    var logDir = ".RAFT"
    var nodeName = ""
    var members = mutableListOf<String>()

    var cacheConfiguration = listOf<EtaCacheConfiguration<*, *>>()

}
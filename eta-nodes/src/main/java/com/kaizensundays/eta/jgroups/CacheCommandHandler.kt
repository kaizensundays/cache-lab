package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.cache.CacheGet
import com.kaizensundays.eta.cache.CachePut
import com.kaizensundays.eta.cache.EtaCache
import com.kaizensundays.eta.cache.LoggingCacheEntryListener
import com.kaizensundays.eta.cache.Msg
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean

/**
 * Created: Saturday 10/5/2024, 1:02 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheCommandHandler(private val cache: EtaCache<String, String>) : InitializingBean {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun afterPropertiesSet() {

        cache.registerCacheEntryListener(
            LoggingCacheEntryListener<String, String>()
                .configuration(isOldValueRequired = false, isSynchronous = false)
        )
    }

    fun get(key: String): String {
        val value = cache[key]
        return if (value != null) cache[key].toString() else ""
    }

    fun put(key: String, value: String) {
        cache.put(key, value)
    }

    fun execute(command: String): String {
        logger.info("command=$command")

        val tokens = command.split(':')
        if (tokens.size > 1) {
            when (tokens[0]) {
                "put" -> {
                    if (tokens.size == 3) {
                        put(tokens[1], tokens[2])
                    }
                }

                "get" -> {
                    if (tokens.size == 2) {
                        get(tokens[1])
                    }
                }

                "remove" -> {
                    //
                }

                else -> {
                    //
                }
            }
        }

        return "Ok"
    }

    fun execute(msg: Msg): String {

        when (msg) {
            is CacheGet -> cache.get(msg.key)
            is CachePut -> cache.put(msg.key, msg.value)
            else -> throw UnsupportedOperationException()
        }

        return "Ok"
    }

}
package com.kaizensundays.eta.jgroups

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Created: Sunday 9/29/2024, 12:28 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@RestController
class DefaultRestController(
    private val cacheCommandHandler: CacheCommandHandler
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/ping")
    fun ping(): String {
        return "Ok"
    }

    @GetMapping("/get/{key}")
    fun get(@PathVariable("key") key: String): String {
        return cacheCommandHandler.get(key)
    }

    @GetMapping("/put/{key}/{value}")
    fun put(@PathVariable("key") key: String, @PathVariable("value") value: String) {
        cacheCommandHandler.put(key, value)
    }

}
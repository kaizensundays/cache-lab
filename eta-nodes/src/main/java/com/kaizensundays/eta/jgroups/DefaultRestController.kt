package com.kaizensundays.eta.jgroups

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * Created: Sunday 9/29/2024, 12:28 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@RestController
class DefaultRestController(
    private val cacheCommandHandler: CacheCommandHandler
) : ApplicationContextAware {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private lateinit var context: ConfigurableApplicationContext

    override fun setApplicationContext(context: ApplicationContext) {
        this.context = context as ConfigurableApplicationContext
    }

    @GetMapping("/ping")
    fun ping(): String {
        return "Ok"
    }

    @GetMapping("/shutdown")
    fun shutdown(): String {

        Mono.fromRunnable<Any> {
            printNonDaemonThreadsOnShutdown()
            logger.info("Shutdown")
            context.close()
            logger.info("Exiting JVM ...")
        }.subscribeOn(Schedulers.boundedElastic()).subscribe()

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
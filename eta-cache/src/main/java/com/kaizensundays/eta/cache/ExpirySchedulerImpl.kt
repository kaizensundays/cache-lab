package com.kaizensundays.eta.cache

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 11/3/2024, 12:02 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ExpirySchedulerImpl<K, V>(private val cache: EtaCache<K, V>) : ExpiryScheduler, Runnable {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private var f: ScheduledFuture<*>? = null

    override fun run() {
        logger.info("Removing expired entries ...")
        cache.removeExpired()
    }

    override fun start() {

        f = executor.scheduleWithFixedDelay(this, 10, 10, TimeUnit.SECONDS)
    }

    override fun stop() {

        f?.cancel(false)

        executor.shutdownNow()
    }

}
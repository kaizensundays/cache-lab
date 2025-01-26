package com.kaizensundays.eta.cache

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.cache.CacheManager
import javax.cache.Caching
import kotlin.math.exp

/**
 * Created: Monday 1/20/2025, 12:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class CacheBenchmark : BenchmarkSupport() {

    private lateinit var manager: CacheManager

    @Setup
    open fun setup() {
        log("Starting cache ...")

        val provider = Caching.getCachingProvider()

        manager = provider.getCacheManager(null, null)

        log("Started")
    }

    @TearDown
    open fun tearDown() {
        Mono.fromRunnable<Any> {
            sleep(1000)
            log("Stopping cache ...")
            manager.close()
            log("Stopped")
        }.subscribeOn(Schedulers.boundedElastic())
            .block(Duration.ofSeconds(60))
    }

    @Benchmark
    open fun measureRight(): Double {
        return exp(1.03)
    }

}
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
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class CacheBenchmark {

    private lateinit var manager: CacheManager

    @Setup
    open fun setup() {
        println("*** setup >")

        val provider = Caching.getCachingProvider()

        manager = provider.getCacheManager(null, null)

        println("*** setup <")
    }

    @TearDown
    open fun tearDown() {
        println("*** tearDown >")
        manager.close()
        println("*** tearDown <")
    }

    @Benchmark
    open fun measureRight(): Double {
        return exp(1.03)
    }

}
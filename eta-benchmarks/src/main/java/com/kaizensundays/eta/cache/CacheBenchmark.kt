package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.LogType
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OperationsPerInvocation
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import site.ycsb.generator.NumberGenerator
import site.ycsb.generator.ScrambledZipfianGenerator
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.Caching

/**
 * Created: Monday 1/20/2025, 12:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class CacheBenchmark : BenchmarkSupport() {

    private var index = 0L

    private lateinit var manager: CacheManager
    private lateinit var cache: Cache<String, String>

    private val NUM_OF_KEYS = 36
    private val GENERATED_MAX_VALUE = NUM_OF_KEYS / 3L

    private lateinit var keys: Array<String>

    @Setup
    open fun setup() {
        log("Starting cache ...")

        index = 0L

        val provider = Caching.getCachingProvider()

        manager = provider.getCacheManager(null, null)

        sleep(3000)

        val conf = EtaCacheConfiguration<String, String>()
        conf.logType = LogType.InMemoryLog

        cache = manager.createCache("default", conf)

        sleep(3000)

        keys = Array(NUM_OF_KEYS) { _ -> "?" }

        val generator: NumberGenerator = ScrambledZipfianGenerator(GENERATED_MAX_VALUE)

        for (i in 0 until NUM_OF_KEYS) {
            val key = generator.nextValue().toString()
            keys[i] = key
            cache.put(key, key)
        }

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
            .subscribe()

        //shutdown(3)
    }

    @Benchmark
    @OperationsPerInvocation(1)
    open fun put(): String {
        val i = (++index % NUM_OF_KEYS).toInt()
        //println("index=$index")
        val key = keys[i]
        cache.put(key, key)
        //sleep(10)
        return key
    }

    @Benchmark
    @OperationsPerInvocation(1)
    open fun get(): String {
        val i = (++index % NUM_OF_KEYS).toInt()
        val key = keys[i]
        return cache[key]
    }

}
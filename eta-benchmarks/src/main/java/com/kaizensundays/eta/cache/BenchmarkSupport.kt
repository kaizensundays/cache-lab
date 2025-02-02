package com.kaizensundays.eta.cache

import org.junit.jupiter.api.Test
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.RunnerException
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import org.openjdk.jmh.runner.options.VerboseMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess

/**
 * Created: Sunday 1/26/2025, 11:24 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class BenchmarkSupport {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    val NEW_LINE = '\n'

    fun done() = logger.info(NEW_LINE + "Done")

    @Test
    @Throws(RunnerException::class)
    fun runTests() {

        val opts = OptionsBuilder()
            .include(".*" + this.javaClass.name + ".*")
            .warmupTime(TimeValue.seconds(3))
            .warmupIterations(warmupCount())
            .measurementTime(TimeValue.seconds(1))
            .measurementIterations(runCount())
            .verbosity(VerboseMode.NORMAL)
            .jvmArgs("-Xms1024m", "-Xmx1024m")
            .forks(0) // don't fork to be able to debug in IDE
            .build()

        Runner(opts).run()
    }

    private fun runCount(): Int {
        return 1
    }

    private fun warmupCount(): Int {
        return 1
    }

    fun log(m: String) {
        println("" + Date() + " " + Thread.currentThread().name + " " + m)
    }

    fun shutdown(delaySec: Int) {

        Thread({
            Thread.sleep(delaySec * 1000L)
            log("Exiting JVM ...")
            exitProcess(0)
        }, "SHUTDOWN").start()
    }
}
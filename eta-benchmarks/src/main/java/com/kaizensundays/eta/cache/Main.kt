package com.kaizensundays.eta.cache

import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder

/**
 * Created: Sunday 1/26/2025, 11:56 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val opts = OptionsBuilder()
            .include(".*Benchmark.*")
            .forks(0)
            .build()

        Runner(opts).run()
    }

}
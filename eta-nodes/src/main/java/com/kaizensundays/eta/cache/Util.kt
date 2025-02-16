package com.kaizensundays.eta.cache

/**
 * Created: Saturday 1/25/2025, 8:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */

fun printNonDaemonThreadsOnShutdown() {

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutdown hook triggered. Listing non-daemon threads:")
        Thread.getAllStackTraces().forEach { (thread: Thread, stackTrace: Array<StackTraceElement>) ->
            if (!thread.isDaemon) {
                println("Non-daemon thread: " + thread.name)
                for (element in stackTrace) {
                    println("    $element")
                }
            }
        }
    })

}

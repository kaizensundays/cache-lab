package com.kaizensundays.eta.jgroups

import org.springframework.boot.SpringApplication

/**
 * Created: Sunday 9/22/2024, 1:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(ServiceContext::class.java, *args)
    }

}
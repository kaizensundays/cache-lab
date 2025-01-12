package com.kaizensundays.eta.cache

import reactor.core.publisher.Flux

/**
 * Created: Monday 12/16/2024, 7:33 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface MessageGrid {

    fun toFlux(topic: String): Flux<String>

    fun send(topic: String, message: String)

}
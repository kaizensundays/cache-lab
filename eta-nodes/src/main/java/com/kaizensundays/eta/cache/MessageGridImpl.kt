package com.kaizensundays.eta.cache

import com.kaizensundays.eta.raft.MessageBus
import reactor.core.publisher.Flux

/**
 * Created: Saturday 12/21/2024, 11:08 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MessageGridImpl(private val messageBus: MessageBus) : MessageGrid {

    override fun toFlux(topic: String): Flux<String> {
        return Flux.create { sink ->
            messageBus.addListener(topic) { msg ->
                sink.next(String(msg))
            }
        }
    }


    override fun send(topic: String, message: String) {
        messageBus.send(topic, message.toByteArray())
    }

}
package com.kaizensundays.eta.cache

import com.kaizensundays.messaging.WebFluxProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.net.URI
import java.time.Duration
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 1/28/2024, 11:31 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TestDriver(private val producer: WebFluxProducer) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val config = Config()

    fun commands1(keyNum: Int, keyMultiplier: Int, rounds: Int, more: Boolean): Flux<String> {
        require(keyNum <= keyMultiplier) { "The number of keys in a round cannot be greater then keyMultiplier" }

        return Flux.range(0, rounds)
            .flatMap { round ->
                Flux.range(0, keyNum + (if (more) 1 else 0)) // fix
                    .flatMap { key ->
                        Flux.fromIterable(
                            listOf(
                                String.format("put:%d:" + config.valueFormat(keyMultiplier), key, round * keyMultiplier + key),
                                String.format("get:%d", key)
                            )
                        )
                    }
            }
    }

    fun commands(keyNum: Int, keyMultiplier: Int, rounds: Int): Flux<String> {
        require(keyNum <= keyMultiplier) { "The number of keys in a round cannot be greater then keyMultiplier" }

        return Flux.create { sink ->
            (0 until rounds).forEach { round ->
                (0 until keyNum).forEach { key ->
                    sink.next(String.format("put:%d:" + config.valueFormat(keyMultiplier), key, round * keyMultiplier + key))
                    sink.next(String.format("get:%d", key))
                }
            }
            sink.complete()
        }
    }

    @Suppress("CallingSubscribeInNonBlockingScope")
    private fun Flux<String>.blockingQueueBuffer(capacity: Int): Flux<String> {

        val queue = ArrayBlockingQueue<String>(capacity)

        this.doOnNext { s -> queue.offer(s) }
            .doOnComplete { queue.offer("") }
            .subscribe()

        return Flux.generate { sink ->
            val s = queue.poll(3, TimeUnit.SECONDS)
            if (s != null) {
                sink.next(s)
            }
        }.takeWhile { s -> s.isNotEmpty() }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun execute(keyNum: Int, messagesPerKey: Int, rounds: Int): Flux<ByteArray> {

        /*
                val loadBalancer = DefaultLoadBalancer(listOf(Instance("localhost", port)))
                val producer = WebFluxProducer(loadBalancer)
        */

        val commands = commands(keyNum, config.keyMultiplier(), rounds)
            .delaySubscription(Duration.ofMillis(config.commandInitialDelayMs))
            .delayElements(Duration.ofMillis(config.commandDelayMs))

        val pub = commands.blockingQueueBuffer(1_000_000)
            .map { it.toByteArray() }

        val total = keyNum * messagesPerKey * rounds

        val topic = URI("ws:/default/ws?maxAttempts=100")

        val t0 = System.currentTimeMillis()

        return producer.request(topic, pub)
            .take(total.toLong())
            .doOnError { e ->
                logger.error("******* ", e)
            }
            .doOnTerminate {
                val t1 = System.currentTimeMillis()
                logger.info("Done {} messages in {} ms", total, t1 - t0)
            }
    }

    fun executeBlocking(keyNum: Int, messagesPerKey: Int, rounds: Int, timeout: Duration) {
        execute(keyNum, messagesPerKey, rounds).blockLast(timeout)
    }

}
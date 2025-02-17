package com.kaizensundays.eta.cache

import com.kaizensundays.messaging.DefaultLoadBalancer
import com.kaizensundays.messaging.Instance
import com.kaizensundays.messaging.WebFluxProducer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep
import java.net.InetAddress
import java.net.URI
import java.net.UnknownHostException
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 11/16/2024, 11:16 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class CacheContainerTestSupport {

    companion object {

        val logger: Logger = LoggerFactory.getLogger(CacheContainerTestSupport::class.java)

        const val KUBE_HOST = "Nevada"

        const val SERVER_PORT = 7701
        val jmxPort: Int = Integer.valueOf(System.getProperty("eta.jmx.port", "0"))
        const val JGROUPS_RAFT_MEMBERS = "A,B,C"

        private val env = mapOf(
            0 to mutableMapOf(
                "SERVER_PORT" to SERVER_PORT.toString(),
                "JGROUPS_RAFT_MEMBERS" to JGROUPS_RAFT_MEMBERS,
                "JGROUPS_RAFT_NODE_NAME" to "A",
            ),
            1 to mutableMapOf(
                "SERVER_PORT" to SERVER_PORT.toString(),
                "JGROUPS_RAFT_MEMBERS" to JGROUPS_RAFT_MEMBERS,
                "JGROUPS_RAFT_NODE_NAME" to "B",
            ),
            2 to mutableMapOf(
                "SERVER_PORT" to SERVER_PORT.toString(),
                "JGROUPS_RAFT_MEMBERS" to JGROUPS_RAFT_MEMBERS,
                "JGROUPS_RAFT_NODE_NAME" to "C",
            ),
        )

        private const val IMAGE = "localhost:32000/eta-cache:latest"

        val containers: List<GenericContainer<*>> = List(3) {
            GenericContainer<Nothing>(DockerImageName.parse(IMAGE))
        }

        @JvmStatic
        fun resolve(hostname: String): String {
            return try {
                InetAddress.getByName(hostname).hostAddress
            } catch (e: UnknownHostException) {
                throw RuntimeException(e)
            }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            val kubeIp = resolve(KUBE_HOST)

            val executor = Executors.newFixedThreadPool(3)

            val network = Network.newNetwork()

            val latch = CountDownLatch(3)

            containers.forEachIndexed { n, container ->
                sleep(n * 10_000L)
                executor.execute {
                    val name = "eta-cache$n"
                    container.withNetwork(network)
                        .withExposedPorts(SERVER_PORT)
                        .withExtraHost(KUBE_HOST, kubeIp)
                        .withEnv(env[n])
                        .waitingFor(Wait.forHttp("/ping").withStartupTimeout(Duration.ofSeconds(100)))
                        .withCreateContainerCmdModifier { cmd ->
                            cmd.withName(name)
                            cmd.withPortSpecs()
                        }
                    if (jmxPort > 0) {
                        val externalJmxPort = 20_000 + jmxPort + n
                        container.portBindings = listOf("$externalJmxPort:${jmxPort}")
                    }
                    container.start()
                    logger.info("Started - $name")
                    latch.countDown()
                }
            }

            latch.await(1000, TimeUnit.SECONDS)
            logger.info("Started all containers")
        }

        @JvmStatic
        @AfterAll
        fun afterClass() {
            Thread.sleep(1_000)
            containers.forEach { container -> container.stop() }
        }

    }

    fun producers(): List<WebFluxProducer> {

        return containers.map { container ->
            val port = container.getMappedPort(SERVER_PORT)
            val jmxPort = if (jmxPort > 0) container.getMappedPort(jmxPort) else 0
            logger.info("port={} jmxPort={}", port, jmxPort)
            val loadBalancer = DefaultLoadBalancer(listOf(Instance(KUBE_HOST, port)))
            WebFluxProducer(loadBalancer)
        }
    }

    fun WebFluxProducer.request(topic: String): String {

        val result = this.request(URI("$topic?maxAttempts=3"))
            .blockLast(Duration.ofSeconds(10))

        return if (result != null) String(result) else ""
    }

}
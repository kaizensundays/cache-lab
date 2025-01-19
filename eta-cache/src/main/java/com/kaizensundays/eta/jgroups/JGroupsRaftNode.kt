package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.LogType
import com.kaizensundays.eta.raft.MessageBus
import com.kaizensundays.eta.raft.RaftNode
import com.kaizensundays.eta.raft.RaftNodeConfiguration
import com.kaizensundays.eta.raft.RaftStateMachine
import com.kaizensundays.eta.raft.RaftStateMachineConfiguration
import com.kaizensundays.eta.raft.RaftStateMachineType
import com.kaizensundays.eta.raft.Value
import org.jgroups.JChannel
import org.jgroups.fork.ForkChannel
import org.jgroups.protocols.Discovery
import org.jgroups.protocols.FD_ALL3
import org.jgroups.protocols.FD_SOCK
import org.jgroups.protocols.FORK
import org.jgroups.protocols.FRAG4
import org.jgroups.protocols.MERGE3
import org.jgroups.protocols.MFC
import org.jgroups.protocols.MPING
import org.jgroups.protocols.PING
import org.jgroups.protocols.TCP
import org.jgroups.protocols.TCPPING
import org.jgroups.protocols.TP
import org.jgroups.protocols.UDP
import org.jgroups.protocols.UFC
import org.jgroups.protocols.UNICAST3
import org.jgroups.protocols.VERIFY_SUSPECT
import org.jgroups.protocols.dns.DNS_PING
import org.jgroups.protocols.pbcast.GMS
import org.jgroups.protocols.pbcast.NAKACK2
import org.jgroups.protocols.pbcast.STABLE
import org.jgroups.protocols.raft.ELECTION
import org.jgroups.protocols.raft.InMemoryLog
import org.jgroups.protocols.raft.NO_DUPES
import org.jgroups.protocols.raft.RAFT
import org.jgroups.protocols.raft.REDIRECT
import org.jgroups.stack.Configurator
import org.jgroups.stack.DiagnosticsHandler
import org.jgroups.stack.NonReflectiveProbeHandler
import org.jgroups.stack.Protocol
import org.jgroups.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.file.Path
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * Created: Sunday 9/22/2024, 1:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsRaftNode : RaftNode {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val lock = ArrayBlockingQueue<Int>(1)

    private lateinit var mainChannel: JChannel
    private lateinit var h: NonReflectiveProbeHandler

    private lateinit var messageBus: JGroupsMessageBus

    private lateinit var configuration: RaftNodeConfiguration

    override fun configure(configuration: RaftNodeConfiguration) {
        this.configuration = configuration
    }

    private fun createChannel(): JChannel {
        val protocols: MutableList<Protocol> = ArrayList()
        val transport: TP = if (configuration.useUDP) UDP() else TCP().setBindPort(7800)
        transport.init()
        if (transport is UDP) {
            transport.getDiagnosticsHandler().enableUdp(true)
        } else {
            transport.diagnosticsHandler.enableUdp(false).enableTcp(true)
        }
        transport.threadPool.setMaxThreads(200)
        transport.diagnosticsHandler.setEnabled(true)
        protocols.add(transport)

        if (configuration.useUDP) {
            protocols.add(PING())
        } else {
            protocols.add(MPING())
            if (System.getProperty("jgroups.dns.dns_query") != null) protocols.add(DNS_PING())
            protocols.add(TCPPING())
        }

        val rest = listOf(
            MERGE3().setMinInterval(10000).setMaxInterval(30000),
            FD_SOCK(),
            FD_ALL3().setTimeout(60000).setInterval(10000),
            VERIFY_SUSPECT(),
            NAKACK2(),
            UNICAST3(),
            STABLE(),
            NO_DUPES(),
            GMS().setJoinTimeout(2000),
            UFC(),
            MFC(),
            FRAG4(),
            FORK(),
            //
            // it could be better to assign address generator to ForkChannel
            // var fc: ForkChannel?
            //    fc?.addAddressGenerator {
            //        ExtendedUUID.setPrintFunction(print_function)
            //        ExtendedUUID.randomUUID(ch.getName()).put(raft_id_key, Util.stringToBytes(raft_id))
            // };
            //
            RAFT().raftId(configuration.nodeName).members(configuration.members)
                .logClass(InMemoryLog::class.java.getCanonicalName())
                .logDir(System.getProperty("log_dir", ".RAFT")),
            //
        )

        protocols.addAll(rest)

        return JChannel(protocols)
    }

    override fun connect() {

        mainChannel.connect("rsm")

        val transport: TP = mainChannel.protocolStack.transport

        val diagnosticsHandler = transport.diagnosticsHandler
        if (diagnosticsHandler != null) {
            val probeHandlers = diagnosticsHandler.probeHandlers
            probeHandlers.removeIf { probeHandler: DiagnosticsHandler.ProbeHandler ->
                val keys = probeHandler.supportedKeys()
                keys != null && Stream.of(*keys).anyMatch { s: String -> s.startsWith("jmx") }
            }
        }
        transport.registerProbeHandler<TP>(h)
    }

    private val logMap = mapOf(
        LogType.InMemoryLog to InMemoryLog::class.java,
        LogType.LevelDBLogExtended to LevelDBLogExtended::class.java,
    )

    private fun logClass(logType: LogType): String {
        return logMap[logType]?.canonicalName ?: throw IllegalArgumentException()
    }

    private fun createForkChannel(conf: RaftStateMachineConfiguration): ForkChannel {
        try {
            val raftProtocol = RAFT()
                .logClass(logClass(conf.logType))
                .logDir(conf.logDir)
                .logPrefix(Path.of(conf.logDir, conf.name, conf.nodeName).toAbsolutePath().toString())
                .members(conf.members)
                .raftId(conf.nodeName)

            return ForkChannel(mainChannel, conf.name, conf.name, ELECTION(), raftProtocol, REDIRECT())
        } catch (e: Exception) {
            logger.error("", e)
            throw IllegalStateException(e)
        }
    }

    override fun <K, V> create(conf: RaftStateMachineConfiguration): RaftStateMachine<K, Value<V?>> {

        val forkChannel = createForkChannel(conf)

        return when (conf.type) {
            RaftStateMachineType.LIB -> {
                throw UnsupportedOperationException()
            }

            RaftStateMachineType.DEFAULT -> {
                JGroupsStateMachine(forkChannel)
            }
        }

    }

    override fun getMessageBus(): MessageBus {
        return messageBus
    }

    override fun init() {
        require(::configuration.isInitialized) { "configure() must be called before init()" }

        Configurator.skipSettingDefaultValues(true) // for GraalVM

        mainChannel = createChannel()

        messageBus = JGroupsMessageBus(mainChannel)

        Configurator.skipSettingDefaultValues(false)

        h = NonReflectiveProbeHandler(mainChannel)
            .initialize(mainChannel.protocolStack.protocols)

        mainChannel.setReceiver(messageBus)

        mainChannel.name = configuration.nodeName

        val stack = mainChannel.protocolStack
        val transport: TP = stack.transport
        transport.registerProbeHandler<TP>(h)

        val ba = if (configuration.bindAddr.isBlank()) {
            Util.getAddress("site_local", Util.getIpStackType())
        } else {
            InetAddress.getByName(configuration.bindAddr)
        }
        val diagAddr = Util.getAddress("224.0.75.75", Util.getIpStackType())
        val mCastAddr = Util.getAddress("228.8.8.8", Util.getIpStackType())
        val mPingMCast = Util.getAddress("230.5.6.7", Util.getIpStackType())

        transport.setBindAddress<TP>(ba).setBindPort<TP>(configuration.bindPort).diagnosticsHandler.setMcastAddress(diagAddr)

        if (transport is UDP) {
            transport.setMulticastAddress<UDP>(mCastAddr)
        }

        var discovery: Discovery? = stack.findProtocol(TCPPING::class.java)
        if (discovery != null) {
            (discovery as TCPPING).initialHosts<TCPPING>(listOf(InetSocketAddress(ba, 7800)))
        }

        discovery = stack.findProtocol(MPING::class.java)
        if (discovery != null) {
            (discovery as MPING).setMcastAddr(mPingMCast)
        }

        Thread {
            while (true) {
                if (lock.poll(60, TimeUnit.SECONDS) != null) {
                    break
                }
                logger.debug(".")
            }
        }.start()

        logger.info("Initialized {}", mainChannel)
    }

    override fun destroy() {

        logger.debug("Unlock")
        lock.put(1)

        logger.info("Stopped")
    }

}
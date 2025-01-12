package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/15/2024, 10:22 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class RaftNodeConfiguration(
    val useUDP: Boolean,
    val bindAddr: String,
    val bindPort: Int,
    var logDir: String,
    var nodeName: String,
    var members: List<String>
)
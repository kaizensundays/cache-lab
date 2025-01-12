package com.kaizensundays.eta.raft

/**
 * Created: Saturday 12/14/2024, 11:19 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
data class RaftStateMachineConfiguration(
    val type: RaftStateMachineType,
    var name: String,
    var nodeName: String,
    var members: List<String>,
    var logType: LogType,
    var logDir: String
)
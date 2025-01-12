package com.kaizensundays.eta.jgroups

import java.io.Serializable

/**
 * Created: Saturday 12/21/2024, 11:26 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsMessage(val topic: String, val msg: ByteArray) : Serializable
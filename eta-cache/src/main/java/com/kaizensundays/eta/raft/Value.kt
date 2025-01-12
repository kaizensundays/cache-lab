package com.kaizensundays.eta.raft

import java.io.Serializable

/**
 * Created: Saturday 12/28/2024, 11:41 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Value<V>(val value: V, val touched: Long) : Serializable
package com.kaizensundays.eta.cache

/**
 * Created: Sunday 2/16/2025, 11:45 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class Msg(val type: String) {
    val seqNum: Int = 0
}
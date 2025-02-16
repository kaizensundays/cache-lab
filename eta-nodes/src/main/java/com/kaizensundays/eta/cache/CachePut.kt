package com.kaizensundays.eta.cache

/**
 * Created: Sunday 2/16/2025, 11:45 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CachePut(val key: String, val value: String) : Msg(MsgType.CachePut.name)
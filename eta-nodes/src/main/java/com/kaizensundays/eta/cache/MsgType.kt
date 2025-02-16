package com.kaizensundays.eta.cache

/**
 * Created: Sunday 2/16/2025, 11:49 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
enum class MsgType(val id: Int) {

    CacheGet(1),
    CachePut(2),
    Response(3),
    CacheValue(4)

}
package com.kaizensundays.eta.cache

/**
 * Created: Sunday 2/16/2025, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CacheValue(val value: String?) : Response(0, "Ok", MsgType.CacheValue.name) {

    constructor() : this(null)
}
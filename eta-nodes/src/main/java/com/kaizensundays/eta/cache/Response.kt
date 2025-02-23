package com.kaizensundays.eta.cache

/**
 * Created: Sunday 2/16/2025, 12:59 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class Response(
    val code: Int,
    val text: String,
    type: String = MsgType.Response
) : Msg(type) {

    constructor() : this(0, "Ok")
}

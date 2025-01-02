package com.kaizensundays.kappa.raft

/**
 * Created: Sunday 12/1/2024, 11:47 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface Condition {

    fun apply(entry: Value<*>, params: Array<Any>): Boolean

}
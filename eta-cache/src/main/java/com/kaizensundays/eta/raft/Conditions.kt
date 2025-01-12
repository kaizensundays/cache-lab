package com.kaizensundays.eta.raft

/**
 * Created: Sunday 12/1/2024, 11:52 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
enum class Conditions(private val condition: Condition) : Condition {

    IF_TOUCHED_BEFORE({ entry, params ->
        if (params.isNotEmpty() && params[0] is Long) {
            val before = params[0] as Long
            entry.touched < before
        } else {
            false
        }
    }),

    IF_MODIFIED_BEFORE({ _, _ ->
        false
    });

    override fun apply(entry: Value<*>, params: Array<Any>): Boolean {
        return condition.apply(entry, params)
    }

    companion object {
        private val map = entries.associateBy { it.ordinal }

        fun fromOrdinal(ordinal: Int): Conditions = map[ordinal] ?: throw IllegalStateException()
    }

}
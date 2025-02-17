package com.kaizensundays.eta.cache

import java.math.BigDecimal
import kotlin.math.log10

/**
 * Created: Monday 2/19/2024, 4:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Config {

    val pos = 3

    val commandInitialDelayMs = 1000L
    val commandDelayMs = 30L

    fun keyMultiplier(pos: Int): Int {
        return BigDecimal.valueOf(10).pow(pos - 1).toInt()
    }

    fun keyMultiplier(): Int {
        return keyMultiplier(pos)
    }

    private fun valueFormatInternal(pos: Int): String {
        return "%0${pos}d"
    }

    fun valueFormat(): String {
        return valueFormatInternal(pos)
    }

    fun valueFormat(keyMultiplier: Int): String {
        return valueFormatInternal(log10(keyMultiplier.toDouble()).toInt() + 1)
    }

}
package com.kaizensundays.eta.raft

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


/**
 * Created: Sunday 12/1/2024, 1:51 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ConditionsTest {

    @Test
    fun ifTouchedBefore() {

        assertTrue(Conditions.IF_TOUCHED_BEFORE.apply(Value("a", 1000), arrayOf(5000L)))
        assertTrue(Conditions.IF_TOUCHED_BEFORE.apply(Value("a", 3000), arrayOf(5000L)))
        assertFalse(Conditions.IF_TOUCHED_BEFORE.apply(Value("a", 7000), arrayOf(5000L)))
    }

}
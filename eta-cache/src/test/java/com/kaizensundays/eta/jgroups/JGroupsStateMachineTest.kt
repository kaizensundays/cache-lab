package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.Conditions
import com.kaizensundays.eta.raft.Value
import org.jgroups.JChannel
import org.jgroups.raft.RaftHandle
import org.jgroups.util.ByteArrayDataInputStream
import org.jgroups.util.ByteArrayDataOutputStream
import org.jgroups.util.Util
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

/**
 * Created: Sunday 12/29/2024, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsStateMachineTest {

    private val ch: JChannel = mock()
    private val raft: RaftHandle = mock()
    private val sm = JGroupsStateMachine<String, Value<String>>(ch, raft)

    @BeforeEach
    fun before() {
    }

    @Suppress("SameParameterValue")
    private fun byteArrayDataInputStream(key: String, params: List<Any>): ByteArrayDataInputStream {
        val outputStream = ByteArrayDataOutputStream(256)
        Util.objectToStream(key, outputStream)
        params.forEach { param ->
            Util.objectToStream(param, outputStream)
        }
        val bytes = outputStream.buffer
        return ByteArrayDataInputStream(bytes)
    }

    @Test
    fun applyRemoveIfRemovesItem() {

        val map = mapOf(
            "a" to Value("A", 1000L),
            "b" to Value("B", 3000L),
            "c" to Value("C", 7000L)
        )
        sm.map.putAll(map)

        map.forEach { (key, valueObj) ->

            val inputStream = byteArrayDataInputStream(
                key, listOf(Conditions.IF_TOUCHED_BEFORE.ordinal, 5000L)
            )

            val response = sm.applyRemoveIf(inputStream, true)
            if (key == "c") {
                assertNull(response)
            } else {
                assertNotNull(response)
                val value = Util.objectFromByteBuffer<Value<String>>(response)
                assertNotNull(value)
                assertEquals(valueObj.value, value.value)
            }
        }

        assertEquals(1, sm.map.size)
    }

}
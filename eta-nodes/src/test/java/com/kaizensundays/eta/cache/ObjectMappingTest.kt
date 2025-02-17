package com.kaizensundays.eta.cache

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * Created: Monday 2/17/2025, 11:23 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ObjectMappingTest {

    private val jsonConverter = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

    fun writeValue(obj: Any): String {
        return jsonConverter.writeValueAsString(obj).replace("\r\n", "\n")
    }

    @Test
    fun convertResponse() {

        val json = listOf(
            """{
            |  "type" : "Response",
            |  "seqNum" : 0,
            |  "code" : 0,
            |  "text" : "Ok"
        |}""",
            """{
            |  "type" : "Response",
            |  "seqNum" : 0,
            |  "code" : 1,
            |  "text" : "System Error"
        |}""",
        ).map { it.trimMargin() }

        listOf(
            Response(0, "Ok"),
            Response(1, "System Error"),
        ).forEachIndexed { i, res ->
            assertEquals(json[i], writeValue(res))
            val msg = jsonConverter.readValue(json[i], Msg::class.java)
            assertTrue(msg is Response)
        }
    }

    @Test
    fun convertCacheValue() {

        val json = listOf(
            """{
            |  "type" : "CacheValue",
            |  "seqNum" : 0,
            |  "code" : 0,
            |  "text" : "Ok",
            |  "value" : "A"
            |}
        """,
            """{
            |  "type" : "CacheValue",
            |  "seqNum" : 0,
            |  "code" : 0,
            |  "text" : "Ok",
            |  "value" : "C"
            |}
        """,
        ).map { it.trimMargin() }

        listOf(
            CacheValue("A"),
            CacheValue("C"),
        ).forEachIndexed { i, obj ->
            assertEquals(json[i], writeValue(obj))
            val msg = jsonConverter.readValue(json[i], Msg::class.java)
            assertTrue(msg is CacheValue)
        }


    }

}
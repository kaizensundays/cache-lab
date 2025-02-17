package com.kaizensundays.eta.cache

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Created: Monday 2/17/2025, 11:23 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ObjectMappingTest {

    private val jsonConverter = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

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
            assertEquals(json[i], jsonConverter.writeValueAsString(res).replace("\r\n", "\n"))
            jsonConverter.readValue(json[i], Msg::class.java)
        }
    }

}
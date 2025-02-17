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
    fun converResponse() {

        val s = jsonConverter.writeValueAsString(Response(0, "Ok"))

        val x = """{
            |  "type" : "Response",
            |  "seqNum" : 0,
            |  "code" : 0,
            |  "text" : "Ok"
        |}"""

        val z = x.trimMargin().replace("\n", "\r\n")

        assertEquals(z, s)
    }

}
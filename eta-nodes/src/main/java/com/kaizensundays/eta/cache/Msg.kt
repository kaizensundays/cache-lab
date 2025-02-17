package com.kaizensundays.eta.cache

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Created: Sunday 2/16/2025, 11:45 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Response::class, name = "Response"),
    JsonSubTypes.Type(value = CacheValue::class, name = "CacheValue"),
    JsonSubTypes.Type(value = CacheGet::class, name = "CacheGet"),
    JsonSubTypes.Type(value = CachePut::class, name = "CachePut"),
)
abstract class Msg(val type: String) {
    val seqNum: Int = 0
}
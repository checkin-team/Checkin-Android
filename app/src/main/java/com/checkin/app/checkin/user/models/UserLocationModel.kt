package com.checkin.app.checkin.user.models

import com.checkin.app.checkin.misc.models.LocationModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserLocationModel(
        @JsonProperty("location") val location: LocationModel,
        @JsonDeserialize(using = LocationTag.LocationTagDeserializer::class)
        @JsonSerialize(using = LocationTag.LocationTagSerializer::class)
        val tag: LocationTag
)

enum class LocationTag(val id: Int) {
    NONE(0),
    WORK(1), HOME(2), OTHER(5), CURRENT(6);

    companion object {
        fun getTag(id: Int): LocationTag = values().find { it.id == id } ?: NONE
    }

    class LocationTagDeserializer : JsonDeserializer<LocationTag>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocationTag = getTag(p.text.toInt())
    }

    class LocationTagSerializer : JsonSerializer<LocationTag>() {
        override fun serialize(value: LocationTag?, gen: JsonGenerator, serializers: SerializerProvider?) {
            value?.id?.let { gen.writeNumber(it) }
        }
    }
}
package com.checkin.app.checkin.Data

import android.util.Log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.objectbox.converter.PropertyConverter
import java.io.IOException

object Converters {
    val objectMapper = jacksonObjectMapper()
    private val TAG = Converters::class.java.simpleName

    fun getJsonNode(json: String): JsonNode? {
        try {
            return objectMapper.readTree(json)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun <T> getObjectFromJson(json: String, typeReference: TypeReference<T>): T? {
        var res: T?
        try {
            res = objectMapper.readValue<T>(json, typeReference)
        } catch (exception: IOException) {
            res = null
            Log.e(TAG, "JSON invalid! $json")
        }

        return res
    }

    class ListConverter<T> : PropertyConverter<List<T>, String> {
        override fun convertToEntityProperty(databaseValue: String): List<T>? {
            val type = object : TypeReference<List<T>>() {}
            return getObjectFromJson(databaseValue, type)
        }

        override fun convertToDatabaseValue(entityProperty: List<T>): String {
            try {
                return objectMapper.writeValueAsString(entityProperty)
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
            }
            return ""
        }
    }
}

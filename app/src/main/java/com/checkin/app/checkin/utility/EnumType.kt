package com.checkin.app.checkin.utility

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.objectbox.converter.PropertyConverter

interface EnumType<out T> {
    val value: T
}

interface EnumIntType : EnumType<Int> {
    companion object {
        inline fun <reified T> getByValue(value: Int): T
                where T : EnumIntType, T : Enum<T> = enumValues<T>().let { values ->
            values.find { value == it.value }
                    ?: throw IllegalArgumentException("Invalid int value $value")
        }
    }
}


interface EnumStringType : EnumType<String> {
    companion object {
        inline fun <reified T> getByValue(value: String): T
                where T : EnumStringType, T : Enum<T> = enumValues<T>().let { values ->
            values.find { value == it.value }
                    ?: throw IllegalArgumentException("Invalid string value $value")
        }
    }
}

sealed class EnumGetter<T : EnumType<V>, V> {
    abstract fun getByValue(value: V?): T?

    abstract fun parseJson(p: JsonParser): V

    abstract fun writeJson(gen: JsonGenerator, value: V)
}

abstract class EnumIntGetter<T : EnumIntType> : EnumGetter<T, Int>() {
    override fun parseJson(p: JsonParser): Int = p.text.toInt()

    override fun writeJson(gen: JsonGenerator, value: Int) = gen.writeNumber(value)
}

abstract class EnumStringGetter<T : EnumStringType> : EnumGetter<T, String>() {
    override fun parseJson(p: JsonParser): String = p.text

    override fun writeJson(gen: JsonGenerator, value: String) = gen.writeString(value)
}

open class EnumDeserializer<T : EnumType<V>, V>(private val getter: EnumGetter<T, V>) : JsonDeserializer<T>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): T? = getter.getByValue(getter.parseJson(p))
}

open class EnumSerializer<T : EnumType<V>, V>(private val getter: EnumGetter<T, V>) : JsonSerializer<T>() {
    override fun serialize(value: T?, gen: JsonGenerator, serializers: SerializerProvider?) {
        value?.value?.let { getter.writeJson(gen, it) }
    }
}

open class EnumConverter<T : EnumType<V>, V>(private val getter: EnumGetter<T, V>) : PropertyConverter<T, V> {
    override fun convertToDatabaseValue(entityProperty: T?): V? = entityProperty?.value

    override fun convertToEntityProperty(databaseValue: V?): T? = getter.getByValue(databaseValue)
}
package com.checkin.app.checkin.restaurant.models

import com.checkin.app.checkin.utility.EnumDeserializer
import com.checkin.app.checkin.utility.EnumIntGetter
import com.checkin.app.checkin.utility.EnumIntType
import com.checkin.app.checkin.utility.EnumSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class RestaurantServiceModel(
        val pk: Long,
        val name: String,
        val logo: String?,
        val locality: String?,
        @JsonProperty("is_preorder") val isPreorder: Boolean,
        @JsonProperty("is_qr") val isQr: Boolean,
        val rating: String?,
        @JsonDeserialize(using = RestaurantServiceType.Companion.Deserializer::class)
        @JsonSerialize(using = RestaurantServiceType.Companion.Serializer::class)
        @JsonProperty("service_type")
        val serviceType: RestaurantServiceType
)

enum class RestaurantServiceType(override val value: Int) : EnumIntType {
    DINEIN(0), QSR(1), HOTEL(5);

    companion object : EnumIntGetter<RestaurantServiceType>() {
        override fun getByValue(value: Int?): RestaurantServiceType? = value?.let { EnumIntType.getByValue(it) }

        class Deserializer : EnumDeserializer<RestaurantServiceType, Int>(this)
        class Serializer : EnumSerializer<RestaurantServiceType, Int>(this)
    }
}
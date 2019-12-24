package com.checkin.app.checkin.restaurant.models

import com.fasterxml.jackson.annotation.JsonProperty

data class RestaurantServiceModel(
        val pk: Long,
        val name: String,
        val logo: String?,
        val locality: String?,
        @JsonProperty("is_preorder") val isPreorder: Boolean,
        @JsonProperty("is_qr") val isQr: Boolean,
        val rating: String?
) {
    lateinit var serviceType: RestaurantServiceType

    @JsonProperty("service_type")
    fun setService(tag: Int) {
        serviceType = RestaurantServiceType.getByTag(tag)
    }
}

enum class RestaurantServiceType(val tag: Int) {
    DINEIN(0), QSR(1);

    companion object {
        fun getByTag(tag: Int) = if (tag == 0) DINEIN else QSR
    }
}
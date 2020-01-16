package com.checkin.app.checkin.misc.models

import android.location.Address
import android.location.Location
import com.checkin.app.checkin.data.Converters.objectMapper
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class LocationModel(
        val pk: Long,
        @JsonProperty("latitude") private var latitudeField: Double?,
        @JsonProperty("longitude") private var longitudeField: Double?,
        val address: String? = null,
        @JsonProperty("pincode") val pinCode: String? = null,
        @JsonProperty("country") val countryCode: String? = null,
        val state: String? = null,
        val city: String? = null,
        val locality: String? = null
) {
    constructor(latitude: Double, longitude: Double) : this(0, latitude, longitude)

    constructor(address: Address) : this(
            0,
            address.latitude, address.longitude,
            address.getAddressLine(0), address.postalCode,
            address.countryCode, address.adminArea,
            address.subAdminArea ?: address.locality ?: address.featureName,
            address.locality ?: address.featureName
    )

    constructor(locality: String?) : this(0, null, null, locality = locality)

    constructor(location: Location) : this(0, location.latitude, location.longitude)

    val latitude: Double
        get() = latitudeField ?: 0.0

    val longitude: Double
        get() = longitudeField ?: 0.0

    @get:JsonProperty("point")
    @set:JsonProperty("point")
    var point: ObjectNode
        get() = objectMapper.createObjectNode()
                .put("latitude", latitude)
                .put("longitude", longitude)
        set(data) {
            latitudeField = data["latitude"].asDouble()
            longitudeField = data["longitude"].asDouble()
        }

    @get:JsonIgnore
    val coordinates: Pair<Double, Double>
        get() = latitude to longitude

    override fun toString(): String = locality ?: address ?: ""
}
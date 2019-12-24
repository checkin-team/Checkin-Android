package com.checkin.app.checkin.restaurant.models

import com.checkin.app.checkin.misc.models.LocationModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
data class RestaurantLocationModel(
        @Id(assignable = true) var pk: Long,
        val name: String,
        val phone: String?,
        val logo: String?,
        @Transient val location: LocationModel?
) {
    val formatAddress: String
        get() = location?.address ?: ""
}
package com.checkin.app.checkin.User

import com.checkin.app.checkin.Misc.LocationModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserLocationModel(
        @JsonProperty("location") val location: LocationModel) {

    lateinit var locationTag: LocationTag

    @JsonProperty("tag")
    fun setTag(id: Int) {
        locationTag = LocationTag.getTag(id)
    }

    @get:JsonProperty("tag")
    val tagCode: Int
        get() = locationTag.id
}

enum class LocationTag(val id: Int) {
    NONE(0),
    WORK(1), HOME(2), OTHER(5), CURRENT(6);

    companion object {
        fun getTag(id: Int): LocationTag = values().find { it.id == id } ?: NONE
    }
}
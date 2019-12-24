package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class NewScheduledSessionModel @JvmOverloads constructor(
        val pk: Long = 0,
        @JsonProperty("count_people") val countPeople: Int? = null,
        @JsonProperty("planned_datetime") val plannedDatetime: Date? = null,
        val remarks: String? = null
) {
    @JsonProperty("restaurant_id")
    var restaurantId: Long = 0
}
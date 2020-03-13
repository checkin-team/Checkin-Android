package com.checkin.app.checkin.home.model

import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.models.SessionBillModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ClosedSessionDetailsModel(
        val pk: Int,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("checkedin_time") val checkedinTime: Date,
        @JsonProperty("checked_out") val checkoutTime: Date,
        @JsonProperty("ordered_items") val orderedItems: List<SessionOrderedItemModel>,
        val restaurant: RestaurantLocationModel,
        val bill: SessionBillModel
) {


    val formatPlannedDate: String = Utils.formatDate(checkedinTime, "hh:mm a | MMM dd, YYYY ")

    val formatId = "#$hashId"

}
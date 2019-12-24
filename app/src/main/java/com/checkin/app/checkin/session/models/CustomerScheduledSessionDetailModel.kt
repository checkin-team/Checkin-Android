package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.fasterxml.jackson.annotation.JsonProperty

data class CustomerScheduledSessionDetailModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        val scheduled: ScheduledSessionDetailModel,
        val restaurant: RestaurantLocationModel,
        val bill: SessionBillModel,
        @JsonProperty("ordered_items") val orderedItems: List<SessionOrderedItemModel>
)
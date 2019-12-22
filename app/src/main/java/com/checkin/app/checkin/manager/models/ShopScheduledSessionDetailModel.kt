package com.checkin.app.checkin.manager.models

import com.checkin.app.checkin.User.UserBriefModel
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.SessionBillModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.fasterxml.jackson.annotation.JsonProperty

data class ShopScheduledSessionDetailModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("ordered_items") val orderedItems: List<SessionOrderedItemModel>,
        val bill: SessionBillModel,
        val scheduled: ScheduledSessionDetailModel,
        val customer: UserBriefModel,
        @JsonProperty("visit_count") val visitCount: Int
) {
    val formatVisitFrequency: String
        get() = when {
            visitCount < 2 -> "New Customer"
            visitCount < 6 -> "Regular Customer"
            else -> "Frequent Customer"
        }
}
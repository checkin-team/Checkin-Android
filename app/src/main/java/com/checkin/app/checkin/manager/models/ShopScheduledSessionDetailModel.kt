package com.checkin.app.checkin.manager.models

import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.models.ScheduledSessionDetailModel
import com.checkin.app.checkin.session.models.SessionBillModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.user.models.UserBriefModel
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ShopScheduledSessionDetailModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("ordered_items") val orderedItems: List<SessionOrderedItemModel>,
        val bill: SessionBillModel,
        val scheduled: ScheduledSessionDetailModel,
        val customer: UserBriefModel,
        @JsonProperty("visit_count") val visitCount: Int
) {
    val customerDueReachTime: String?
        get() {
            val current = Calendar.getInstance().time
            return Utils.formatDueTime(current, scheduled.plannedDatetime!!).takeIf { current <= scheduled.plannedDatetime }
        }
    val customerLateTime: String?
        get() {
            val current = Calendar.getInstance().time
            return Utils.formatDueTime(scheduled.plannedDatetime!!, current).takeIf { current > scheduled.plannedDatetime }
        }

    val formatVisitFrequency: String
        get() = when {
            visitCount < 2 -> "New Customer"
            visitCount < 6 -> "Regular Customer"
            else -> "Frequent Customer"
        }
}
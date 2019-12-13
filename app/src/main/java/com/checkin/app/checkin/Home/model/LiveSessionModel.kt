package com.checkin.app.checkin.Home.model

import com.checkin.app.checkin.Menu.Model.OrderedItemStatusModel
import com.checkin.app.checkin.Shop.RestaurantLocationModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.model.ScheduledSessionBriefModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

sealed class LiveSessionDetail

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledLiveSessionDetailModel(
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("ordered_items") val orderedItems: List<OrderedItemStatusModel>,
        @JsonProperty("count_orders") val countOrders: Int,
        val scheduled: ScheduledSessionBriefModel,
        @JsonProperty("paid_amount") val paidAmount: Double,
        val restaurant: RestaurantLocationModel,
        @JsonProperty("is_pre_dining") val isPreDining: Boolean
) : LiveSessionDetail() {
    val formatSessionId: String
        get() = "#$hashId"

    val isOrderInProgress: Boolean
        get() = orderedItems.map { it.status }.any {
            when (it) {
                SessionChatModel.CHAT_STATUS_TYPE.OPEN, SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS -> true
                else -> false
            }
        }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ActiveLiveSessionDetailModel(
        val pk: Long
) : LiveSessionDetail()

package com.checkin.app.checkin.Home.model

import com.checkin.app.checkin.Menu.Model.OrderedItemStatusModel
import com.checkin.app.checkin.Shop.RestaurantLocationModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.model.PromoBriefModel
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
        @JsonProperty("paid_amount") val amount: Double,
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
        val pk: Long,
        @JsonProperty("hash_id") val hashId: String,
        @JsonProperty("ordered_items") val orderedItems: List<OrderedItemStatusModel>,
        @JsonProperty("count_orders") val countOrders: Int,
        val amount: Double,
        val restaurant: RestaurantLocationModel,
        val offers: List<PromoBriefModel>
) : LiveSessionDetail() {

    val newOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.OPEN }.count()

    val progressOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS || it.status == SessionChatModel.CHAT_STATUS_TYPE.COOKED }.count()

    val doneOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.DONE }.count()
}

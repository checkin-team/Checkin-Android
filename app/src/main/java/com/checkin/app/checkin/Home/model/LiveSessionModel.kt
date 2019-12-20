package com.checkin.app.checkin.Home.model

import com.checkin.app.checkin.menu.models.OrderedItemStatusModel
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.PromoBriefModel
import com.checkin.app.checkin.session.models.ScheduledSessionBriefModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

sealed class LiveSessionDetailModel(
        open val pk: Long,
        open val hashId: String,
        open val orderedItems: List<OrderedItemStatusModel>,
        open val countOrders: Int,
        open val restaurant: RestaurantLocationModel
) {
    val formatSessionId: String
        get() = "#$hashId"

    val newOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.OPEN }.count()

    val progressOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS || it.status == SessionChatModel.CHAT_STATUS_TYPE.COOKED }.count()

    val doneOrdersCount: Int
        get() = orderedItems.filter { it.status == SessionChatModel.CHAT_STATUS_TYPE.DONE }.count()

    val isOrderInProgress: Boolean
        get() = orderedItems.map { it.status }.any {
            when (it) {
                SessionChatModel.CHAT_STATUS_TYPE.OPEN, SessionChatModel.CHAT_STATUS_TYPE.IN_PROGRESS -> true
                else -> false
            }
        }

    val sessionType: SessionType
        get() = when (this) {
            is ScheduledLiveSessionDetailModel -> if (isPreDining) SessionType.PREDINING else SessionType.QSR
            else -> SessionType.DINING
        }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduledLiveSessionDetailModel(
        override val pk: Long,
        @JsonProperty("hash_id") override val hashId: String,
        @JsonProperty("ordered_items") override val orderedItems: List<OrderedItemStatusModel>,
        @JsonProperty("count_orders") override val countOrders: Int,
        override val restaurant: RestaurantLocationModel,
        val scheduled: ScheduledSessionBriefModel,
        @JsonProperty("paid_amount") val amount: Double,
        @JsonProperty("is_pre_dining") val isPreDining: Boolean
) : LiveSessionDetailModel(pk, hashId, orderedItems, countOrders, restaurant)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ActiveLiveSessionDetailModel(
        override val pk: Long,
        @JsonProperty("hash_id") override val hashId: String,
        @JsonProperty("ordered_items") override val orderedItems: List<OrderedItemStatusModel>,
        @JsonProperty("count_orders") override val countOrders: Int,
        override val restaurant: RestaurantLocationModel,
        val amount: Double,
        val offers: List<PromoBriefModel>
) : LiveSessionDetailModel(pk, hashId, orderedItems, countOrders, restaurant)

enum class SessionType {
    DINING, QSR, PREDINING
}

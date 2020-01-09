package com.checkin.app.checkin.Data.Message

import com.checkin.app.checkin.Data.Converters.objectMapper
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE
import com.checkin.app.checkin.User.UserModel.GENDER
import com.checkin.app.checkin.manager.models.ManagerSessionEventModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageDataModel(
        @JsonProperty("session__bill__total") val sessionBillTotal: Double?,
        @JsonProperty("session__customer_count") val sessionCustomerCount: Int?,
        @JsonProperty("session__table") val sessionTableName: String?,
        @JsonProperty("session__order_id") val sessionOrderId: Long?,
        @JsonProperty("session__ordered_item") val sessionOrderedItem: SessionOrderedItemModel?,
        @JsonProperty("session__event_detail") val sessionEventDetail: SessionChatModel?,
        @JsonProperty("session__event") val sessionEventBrief: ManagerSessionEventModel?,
        @JsonProperty("session__qr_id") val sessionQRId: Long?,
        @JsonProperty("session__new_qr_table") val sessionNewTable: String?,
        @JsonProperty("session__new_qr_id") val sessionNewQrId: Long?
) : Serializable {
    var userGender: GENDER? = null
        private set
    var sessionBillPaymentMode: PAYMENT_MODE? = null
        private set
    var sessionEventStatus: CHAT_STATUS_TYPE? = null
        private set

    @JsonProperty("user__gender")
    fun setUserGender(gender: Char) {
        userGender = GENDER.getByTag(gender)
    }

    @JsonProperty("session__bill__payment_mode")
    fun setSessionPaymentMode(mode: String) {
        sessionBillPaymentMode = PAYMENT_MODE.getByTag(mode)
    }

    @JsonProperty("session__event__status")
    fun setSessionEventStatus(status: Int) {
        sessionEventStatus = CHAT_STATUS_TYPE.getByTag(status)
    }

    class MessageDataDeserializer : JsonDeserializer<MessageDataModel>() {
        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): MessageDataModel {
            return objectMapper.readValue(jsonParser.text, MessageDataModel::class.java)
        }
    }
}
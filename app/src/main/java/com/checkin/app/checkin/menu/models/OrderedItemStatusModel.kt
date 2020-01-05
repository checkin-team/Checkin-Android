package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderedItemStatusModel(
        val pk: Long,
        val name: String,
        val quantity: Int
) {
    lateinit var status: SessionChatModel.CHAT_STATUS_TYPE

    @JsonProperty("status")
    fun setStatus(statusCode: Int) {
        status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(statusCode)
    }

    @get:JsonProperty("status")
    val statusCode: Int
        get() = status.tag
}

package com.checkin.app.checkin.Menu.Model

import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderedItemStatusModel(
        val pk: Long,
        val name: String,
        val status: SessionChatModel.CHAT_STATUS_TYPE,
        val quantity: Int
)

package com.checkin.app.checkin.menu.models

import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CartDetailModel(
        val pk: Long,
        @JsonProperty("ordered_items") val orderedItems: List<SessionOrderedItemModel>
)
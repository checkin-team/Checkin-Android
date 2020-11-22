package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonProperty

data class SessionPromoModel(
        val code: String,
        val icon: String?,
        val name: String,
        @JsonProperty("by_user") val byUser: BriefModel,
        @JsonProperty("offer_amount") val offerAmount: Double?
) {
    val details: String
        get() = String.format("%s - %s", code, Utils.fromHtml(name))
}
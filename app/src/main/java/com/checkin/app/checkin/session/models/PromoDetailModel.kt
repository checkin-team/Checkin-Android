package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonProperty

data class PromoDetailModel(
        val pk: Long,
        val code: String,
        val icon: String?,
        val name: String,
        val summary: String?,
        val terms: String?,
        val expires: String?,
        @JsonProperty("discount_amount") val discountAmount: Double
) {
    val formatName: CharSequence = Utils.fromHtml(name)
}
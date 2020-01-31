package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PromoBriefModel(
        val code: String,
        val icon: String?,
        val name: String
) {
    val formatName: CharSequence = Utils.fromHtml(name)
}

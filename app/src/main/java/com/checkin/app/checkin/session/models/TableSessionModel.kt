package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TableSessionModel(
        @JsonProperty("pk") val pk: Long = 0
) {
    @JsonProperty("host")
    var host: BriefModel? = null

    @JsonProperty("event")
    lateinit var event: EventBriefModel

    @JsonProperty("created")
    lateinit var created: Date

    @JsonProperty("is_accepted_checkout")
    var isRequestedCheckout: Boolean = false

    @JsonProperty("bill")
    var bill: Double = 0.0

    @JsonProperty("pending_orders")
    val pendingOrders: Int = 0

    constructor(pk: Long, host: BriefModel?, event: EventBriefModel) : this(pk) {
        this.host = host
        this.event = event
    }

    fun hasHost(): Boolean {
        return host != null
    }

    fun formatTimeDuration(): String {
        return Utils.formatTimeDuration(Calendar.getInstance().time.time - created.time)
    }
}
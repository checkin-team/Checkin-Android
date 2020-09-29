package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.utility.Utils
import com.fasterxml.jackson.annotation.JsonProperty
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class TableSessionModel(
        @Id(assignable = true) var pk: Long = 0
) {
    @Transient
    var host: BriefModel? = null

    @Transient
    var event: EventBriefModel? = null

    lateinit var created: Date

    @JsonProperty("is_accepted_checkout")
    var isRequestedCheckout: Boolean = false

    var bill: Double = 0.0

    @JsonProperty("pending_orders")
    var pendingOrders: Int = 0

    constructor(pk: Long, host: BriefModel?, event: EventBriefModel) : this(pk) {
        this.host = host
        this.event = event
        this.created = Date()
    }

    fun hasHost(): Boolean = host != null

    fun formatTimeDuration(): String {
        return Utils.formatTimeDuration(Calendar.getInstance().time.time - created.time)
    }
}
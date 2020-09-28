package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class RestaurantTableModel(
        @Id(assignable = true) @JsonProperty("qr_pk") var qrPk: Long,
        val table: String? = null,
        @Transient @JsonProperty("session") val tableSession: TableSessionModel? = null
) {
    var eventCount: Int = 0

    val isSessionActive: Boolean = tableSession != null

    val formatEventCount = eventCount.toString()

    val sessionPk: Long? = tableSession?.pk

    @JsonIgnore
    lateinit var relTableSession: ToOne<TableSessionModel>

    init {
        relTableSession.target = tableSession
    }

    // saved only in local DB
    @JsonIgnore
    var restaurantPk: Long = 0

    fun addEvent(event: EventBriefModel) {
        tableSession?.event = event
        eventCount++
    }

    fun resetEvents() {
        eventCount = 0
    }

    val formatOrderStatus: String
        get() = when {
            eventCount > 0 -> "New Order!"
            tableSession?.pendingOrders ?: 0 > 0 -> String.format(Locale.getDefault(), "%d orders in line. Mark ready after preparation.", tableSession!!.pendingOrders)
            else -> "No active orders."
        }

    companion object {
        const val NO_QR_ID: Long = -1
    }
}

package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.data.db.dbStore
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class RestaurantTableModel(
        @Id(assignable = true) @JsonProperty("qr_pk") var qrPk: Long = 0,
        val table: String? = null
) {
    constructor(qrPk: Long, table: String?, sessionModel: TableSessionModel?) : this(qrPk, table) {
        setSession(sessionModel)
    }

    @delegate:Transient
    val tableSession: TableSessionModel? by lazy { relTableSession.target }

    var eventCount: Int = 0

    @delegate:Transient
    val isSessionActive: Boolean by lazy { tableSession != null }

    val formatEventCount = eventCount.toString()

    @delegate:Transient
    val sessionPk: Long? by lazy { tableSession?.pk }

    lateinit var relTableSession: ToOne<TableSessionModel>

    // saved only in local DB
    @JsonIgnore
    var restaurantPk: Long = 0

    @JsonSetter("session")
    fun setSession(sessionModel: TableSessionModel?) {
        tableBox.attach(this)
        if (!relTableSession.isNull) sessionBox.remove(relTableSession.targetId)
        if (sessionModel != null) sessionBox.put(sessionModel)
        relTableSession.target = sessionModel
        tableBox.put(this)
    }

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

        private val tableBox by dbStore<RestaurantTableModel>()
        private val sessionBox by dbStore<TableSessionModel>()
    }
}

package com.checkin.app.checkin.session.models

import com.checkin.app.checkin.data.db.dbStore
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
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
        commitSession(sessionModel)
    }

    @delegate:Transient
    val tableSession: TableSessionModel? by lazy { relTableSession.target }

    @delegate:Transient
    val isSessionActive: Boolean by lazy { tableSession != null }

    @delegate:Transient
    val sessionPk: Long? by lazy { tableSession?.pk }

    // below are saved only in local DB
    var restaurantPk: Long = 0
    var unseenEventCount: Int = 0
    var lastEventTimestamp: Date? = null

    lateinit var relTableSession: ToOne<TableSessionModel>

    val formatEventCount
        get() = unseenEventCount.toString()

    @JsonSetter("session")
    fun setSession(sessionModel: TableSessionModel?) {
        if (!relTableSession.isNull) sessionBox.remove(relTableSession.targetId)
        if (sessionModel != null) sessionBox.put(sessionModel)
        relTableSession.target = sessionModel
        lastEventTimestamp = sessionModel?.event?.timestamp
    }

    fun commitSession(sessionModel: TableSessionModel?) = inTransaction { setSession(sessionModel) }

    fun addEvent(event: EventBriefModel) = inTransaction {
        tableSession?.event = event
        unseenEventCount++
        lastEventTimestamp = event.timestamp
        // increase pending orders if order event
        if (event.type == SessionChatModel.CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM) tableSession?.pendingOrders?.inc()
    }

    fun resetEvents() = inTransaction {
        unseenEventCount = 0
    }

    fun removeFromDb() {
        tableSession?.also { sessionBox.remove(it) }
        tableBox.remove(this)
    }

    fun addToDb() {
        tableBox.put(this)
    }

    /**
     * Only to be used for cook screen
     * since cook events are always related to order
     */
    val formatOrderStatus: String
        get() = when {
            unseenEventCount > 0 -> "New Order!"
            tableSession?.pendingOrders ?: 0 > 0 -> String.format(Locale.getDefault(), "<font color=#ff4f19><b>%d orders in line.</b></font> Mark ready after preparation.", tableSession!!.pendingOrders)
            else -> "No active orders."
        }

    companion object {
        private val tableBox by dbStore<RestaurantTableModel>()
        private val sessionBox by dbStore<TableSessionModel>()

        private fun RestaurantTableModel.inTransaction(block: RestaurantTableModel.() -> Unit) {
            tableBox.attach(this)
            block()
            tableBox.put(this)
        }

        val sortComparator = Comparator<RestaurantTableModel> { t1, t2 ->
            val t1EventTime = t1.lastEventTimestamp ?: t1.tableSession?.event?.timestamp
            val t2EventTime = t2.lastEventTimestamp ?: t2.tableSession?.event?.timestamp
            if (t1EventTime != null && t2EventTime != null) {
                t2EventTime.compareTo(t1EventTime)
            } else if (t2.tableSession != null && t1.tableSession != null) {
                t2.tableSession!!.created.compareTo(t1.tableSession!!.created)
            } else 0
        }
    }
}

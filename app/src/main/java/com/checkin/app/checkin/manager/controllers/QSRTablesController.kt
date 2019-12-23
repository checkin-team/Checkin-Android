package com.checkin.app.checkin.manager.controllers

import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.manager.holders.QSRTableInteraction
import com.checkin.app.checkin.manager.holders.endQsrTableModelHolder
import com.checkin.app.checkin.manager.holders.newQsrTableModelHolder
import com.checkin.app.checkin.manager.holders.preparationQsrTableModelHolder
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController
import com.checkin.app.checkin.misc.holders.textSectionModelHolder
import com.checkin.app.checkin.session.models.ScheduledSessionStatus

class QSRTablesController(
        val listener: QSRTableInteraction
) : BaseEpoxyController() {

    var sessions: List<ShopScheduledSessionModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    val newSessions: List<ShopScheduledSessionModel>?
        get() = sessions?.filter { it.scheduled.status == ScheduledSessionStatus.PENDING }

    val preparationSessions: List<ShopScheduledSessionModel>?
        get() = sessions?.filter { it.scheduled.status == ScheduledSessionStatus.PREPARATION }

    val endSessions: List<ShopScheduledSessionModel>?
        get() = sessions?.filter { it.scheduled.status == ScheduledSessionStatus.DONE }

    override fun buildModels() {
        newSessions.takeIf { it.isNotEmpty() }?.let {
            textSectionModelHolder {
                id("table.new")
                heading("New Orders")
            }
            it.forEach {
                newQsrTableModelHolder {
                    id("new", it.pk)
                    scheduledSessionModel(it)
                    listener(listener)
                }
            }
        }

        preparationSessions.takeIf { it.isNotEmpty() }?.let {
            textSectionModelHolder {
                id("table.preparation")
                heading("Orders in Kitchen")
            }
            it.forEach {
                preparationQsrTableModelHolder {
                    id("preparation", it.pk)
                    scheduledSessionModel(it)
                    listener(listener)
                }
            }
        }

        endSessions.takeIf { it.isNotEmpty() }?.let {
            textSectionModelHolder {
                id("table.end")
                heading("Food Served")
            }
            it.forEach {
                endQsrTableModelHolder {
                    id("upcoming", it.pk)
                    scheduledSessionModel(it)
                    listener(listener)
                }
            }
        }
    }
}
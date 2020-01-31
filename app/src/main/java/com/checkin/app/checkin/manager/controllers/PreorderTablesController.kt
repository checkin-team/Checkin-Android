package com.checkin.app.checkin.manager.controllers

import com.checkin.app.checkin.manager.holders.PreorderTableInteraction
import com.checkin.app.checkin.manager.holders.newPreorderTableModelHolder
import com.checkin.app.checkin.manager.holders.preparationPreorderTableModelHolder
import com.checkin.app.checkin.manager.holders.upcomingPreorderTableModelHolder
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyController
import com.checkin.app.checkin.misc.holders.textSectionModelHolder
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.utility.isNotEmpty

class PreorderTablesController(
        val listener: PreorderTableInteraction
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

    val upcomingSessions: List<ShopScheduledSessionModel>?
        get() = sessions?.filter { it.scheduled.status == ScheduledSessionStatus.ACCEPTED }

    override fun buildModels() {
        newSessions.takeIf { it.isNotEmpty() }?.let {
            textSectionModelHolder {
                id("table.new")
                heading("New Orders")
            }
            it.forEach {
                newPreorderTableModelHolder {
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
                preparationPreorderTableModelHolder {
                    id("preparation", it.pk)
                    scheduledSessionModel(it)
                    listener(listener)
                }
            }
        }

        upcomingSessions.takeIf { it.isNotEmpty() }?.let {
            textSectionModelHolder {
                id("table.upcoming")
                heading("Upcoming Orders")
            }
            it.forEach {
                upcomingPreorderTableModelHolder {
                    id("upcoming", it.pk)
                    scheduledSessionModel(it)
                    listener(listener)
                }
            }
        }
    }
}
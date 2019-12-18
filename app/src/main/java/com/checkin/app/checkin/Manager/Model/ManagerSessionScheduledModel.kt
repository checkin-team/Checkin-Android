package com.checkin.app.checkin.Manager.Model

import com.checkin.app.checkin.session.model.ScheduledSessionStatus

class ManagerSessionScheduledModel(var costumer_name:String,var image_url:String,var date:String,var time:String,var table:Int){
    lateinit var status: ScheduledSessionStatus

    init {

    }
    fun getFormatedDayTime(): String{
        return date+"| "+time +" | "+"table no ${table}"
    }

}
enum class ManagerScheduledStatus(val id: Int) {
   NONE(0), CURRENT(0), ARRIVING(3), SCHEDULED(8);

    val repr: String
        get() = when (this) {
            CURRENT -> "Pending"
            ARRIVING -> "Accepted"
           SCHEDULED -> "Cancelled"
            else->"Unknown"
        }

    companion object {
        fun getById(id: Int): ManagerScheduledStatus = values().find { it.id == id } ?: NONE
    }
}


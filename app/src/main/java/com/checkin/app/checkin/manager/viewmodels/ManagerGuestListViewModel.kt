package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Waiter.WaiterRepository
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.GuestDetailsModel

class ManagerGuestListViewModel(application: Application) : BaseViewModel(application) {
    private val waiterRepository = WaiterRepository.getInstance(application)

    private val guestListLiveData = createNetworkLiveData<List<GuestDetailsModel>>()

    val guestLiveData: LiveData<Resource<List<GuestDetailsModel>>> = guestListLiveData

    val guestList = arrayListOf(GuestDetailsModel("+91", ""))
    lateinit var tableNumber: String
    var qrpk: Long = -1
    var isGuestAdded = false

    fun addGuestList(sessionId: Long) {
        Log.d("HELLO", "This is two")
        guestListLiveData.addSource(waiterRepository.postSessionContact(sessionId, guestList), guestListLiveData::setValue)
    }

    fun setGuestAdded() {
        isGuestAdded = true
    }


    override fun updateResults() {

    }

}
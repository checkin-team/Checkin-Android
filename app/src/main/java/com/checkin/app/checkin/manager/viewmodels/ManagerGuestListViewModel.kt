package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Waiter.WaiterRepository
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.models.GuestContactModel

class ManagerGuestListViewModel(application: Application) : BaseViewModel(application) {
    private val waiterRepository = WaiterRepository.getInstance(application)

    private val mGuestLiveData = createNetworkLiveData<List<GuestContactModel>>()

    val guestLiveData: LiveData<Resource<List<GuestContactModel>>> = mGuestLiveData

    val mvGuestContacts = arrayListOf(GuestContactModel("", ""))

    var isGuestAdded = false
        private set

    fun postGuestList(sessionId: Long) {
        mGuestLiveData.addSource(waiterRepository.postSessionContacts(sessionId, mvGuestContacts), mGuestLiveData::setValue)
    }

    fun setGuestAdded() {
        isGuestAdded = true
    }

    fun addGuest(contact: GuestContactModel) = mvGuestContacts.add(contact)

    override fun updateResults() {
    }
}
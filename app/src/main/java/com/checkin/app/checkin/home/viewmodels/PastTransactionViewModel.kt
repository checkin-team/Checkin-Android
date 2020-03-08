package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.HomeRepository
import com.checkin.app.checkin.session.models.CustomerPastSessionModel

class PastTransactionViewModel(application: Application) : BaseViewModel(application) {

    private val homeRepository = HomeRepository.getInstance(application)

    private val mCustomerPastTransaction = createNetworkLiveData<List<CustomerPastSessionModel>>()

    val customerPastTransaction: LiveData<Resource<List<CustomerPastSessionModel>>> = mCustomerPastTransaction

    fun fetchCustomerTransaction() {
        mCustomerPastTransaction.addSource(homeRepository.customerPastSessionList, mCustomerPastTransaction::setValue)
    }

    override fun updateResults() {
        fetchCustomerTransaction()
    }

}
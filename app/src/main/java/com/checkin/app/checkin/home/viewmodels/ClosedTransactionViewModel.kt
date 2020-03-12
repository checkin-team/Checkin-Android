package com.checkin.app.checkin.home.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.HomeRepository
import com.checkin.app.checkin.home.model.CustomerClosedSessionModel

class ClosedTransactionViewModel(application: Application) : BaseViewModel(application) {

    private val homeRepository = HomeRepository.getInstance(application)

    private val mCustomerClosedSessionModel = createNetworkLiveData<List<CustomerClosedSessionModel>>()

    val customerClosedTransaction: LiveData<Resource<List<CustomerClosedSessionModel>>> = mCustomerClosedSessionModel

    fun fetchCustomerTransaction() {
        mCustomerClosedSessionModel.addSource(homeRepository.customerClosedSessionList, mCustomerClosedSessionModel::setValue)
    }

    override fun updateResults() {
        fetchCustomerTransaction()
    }

}
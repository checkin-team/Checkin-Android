package com.checkin.app.checkin.menu.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.models.NewOrderModel
import com.checkin.app.checkin.utility.isNotEmpty

class ActiveSessionCartViewModel(application: Application) : BaseCartViewModel(application) {
    private val mNewOrders = createNetworkLiveData<List<NewOrderModel>>()

    val serverOrders: LiveData<Resource<List<NewOrderModel>>> = mNewOrders

    fun confirmOrder() {
        mOrderedItems.value?.let {
            if (it.isNotEmpty()) {
                if (sessionPk == 0L)
                    mNewOrders.addSource(menuRepository.postActiveSessionOrders(it), mNewOrders::setValue)
                else
                    mNewOrders.addSource(menuRepository.postManageSessionOrders(sessionPk, it), mNewOrders::setValue)
            }
        }
    }

    override fun updateResults() {
    }
}
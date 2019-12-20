package com.checkin.app.checkin.misc

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Resource

class BlockingNetworkViewModel(application: Application) : BaseViewModel(application) {
    private val mNetworkData = createNetworkLiveData<Any?>()
    private val mTryAgain = MutableLiveData<String>()
    private var mResourceDataClass: String? = null

    val networkBlockingData = mNetworkData
    val shouldTryAgain: LiveData<String> = mTryAgain

    fun <T : Any> updateStatus(resource: Resource<T>) {
        mResourceDataClass = resource.data?.javaClass?.simpleName
        mNetworkData.value = resource
    }

    fun tryAgain() {
        mTryAgain.value = mResourceDataClass
    }

    fun tried() {
        mTryAgain.value = null
    }

    override fun updateResults() {
    }
}
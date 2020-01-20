package com.checkin.app.checkin.misc

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource

class BlockingNetworkViewModel(application: Application) : BaseViewModel(application) {
    private val mNetworkData = createNetworkLiveData<Any?>()
    private val mTryAgain = MutableLiveData<String>()
    private var mResourceDataClass: String? = null

    val networkBlockingData = mNetworkData
    val shouldTryAgain: LiveData<String> = mTryAgain

    fun <T : Any> updateStatus(resource: Resource<T>?, key: String? = null) = resource?.let {
        if (mResourceDataClass != key && mNetworkData.value?.inError == true) return@let
        mResourceDataClass = key ?: it.data?.javaClass?.simpleName
        mNetworkData.value = it
        if (resource.status == Resource.Status.SUCCESS) tried()
    }

    fun <T : Any> updateStatusForOnlyError(resource: Resource<T>?, key: String? = null) = resource?.let {
        if (mNetworkData.value?.inError == true && it.status != Resource.Status.LOADING && key == mResourceDataClass || it.inError)
            updateStatus(it, key)
    }

    fun resetStatus() {
        mNetworkData.value = null
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
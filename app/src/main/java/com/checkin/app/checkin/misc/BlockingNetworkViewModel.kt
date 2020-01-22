package com.checkin.app.checkin.misc

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@UseExperimental(ExperimentalCoroutinesApi::class)
class BlockingNetworkViewModel(application: Application) : BaseViewModel(application) {
    private val mNetworkData = createNetworkLiveData<Any?>()
    private val tryAgainChannel = ConflatedBroadcastChannel<String?>()
    private var mResourceDataClass: String? = null

    val networkBlockingData = mNetworkData

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

    // Uses Broadcast channel to deal with the action to execute in a suspending function
    fun shouldTryAgain(action: (String?) -> Unit) = viewModelScope.launch {
        tryAgainChannel.openSubscription().consumeEach(action)
    }

    fun resetStatus() {
        mNetworkData.value = null
    }

    fun tryAgain() {
        tryAgainChannel.offer(mResourceDataClass)
    }

    fun tried() {
    }

    override fun updateResults() {
    }
}
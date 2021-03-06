package com.checkin.app.checkin.data

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.network.ProblemHandler
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.utility.SourceMappedLiveData
import com.fasterxml.jackson.databind.node.ObjectNode

interface IBaseViewModel {
    fun <T> createNetworkLiveData(): SourceMappedLiveData<Resource<T>>
}

abstract class BaseViewModel(private val mApplication: Application) : AndroidViewModel(mApplication), IBaseViewModel {
    protected var mData = createNetworkLiveData<ObjectNode>()

    val observableData: LiveData<Resource<ObjectNode>>
        get() = mData

    final override fun <T> createNetworkLiveData(): SourceMappedLiveData<Resource<T>> = SourceMappedLiveData { resource ->
        if (ProblemHandler.handleProblems(mApplication, resource)) null else resource
    }

    fun resetObservableData() {
        mData.value = null
    }

    abstract fun updateResults()

    open fun fetchMissing() {}

    @CallSuper
    open fun resetData() {
    }
}

package com.checkin.app.checkin.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.checkin.app.checkin.Utility.ProblemHandler
import com.checkin.app.checkin.Utility.SourceMappedLiveData
import com.fasterxml.jackson.databind.node.ObjectNode

abstract class BaseViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    protected var mData = createNetworkLiveData<ObjectNode>()

    val observableData: LiveData<Resource<ObjectNode>>
        get() = mData

    protected fun <T> createNetworkLiveData(): SourceMappedLiveData<Resource<T>> = SourceMappedLiveData { resource ->
        if (ProblemHandler.handleProblems(mApplication, resource)) null else resource
    }

    fun resetObservableData() {
        mData.value = null
    }

    abstract fun updateResults()
}

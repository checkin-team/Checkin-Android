package com.checkin.app.checkin.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class RetrofitLiveData<T>(private val mCall: Call<T>) : LiveData<ApiResponse<T>>() {
    private var mResponseDispatched = false
    private val mCallback: Callback<T> = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            postValue(ApiResponse(response))
            mResponseDispatched = true
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            postValue(ApiResponse(t))
        }
    }
    private val shouldCancel: Boolean
        get() = !hasObservers()

    override fun onActive() {
        super.onActive()
        if (!mResponseDispatched) {
            if (!mCall.isExecuted && !mCall.isCanceled) mCall.enqueue(mCallback) else mCall.clone().enqueue(mCallback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (shouldCancel) cancel()
    }

    protected fun cancel() {
        if (!mCall.isCanceled) Log.d("Http Calls", "Call cancelled!")
        mCall.cancel()
    }
}
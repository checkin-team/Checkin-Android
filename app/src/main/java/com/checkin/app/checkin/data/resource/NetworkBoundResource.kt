package com.checkin.app.checkin.data.resource

import android.os.AsyncTask
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.resource.Resource.Companion.cloneResource
import com.checkin.app.checkin.data.resource.Resource.Companion.createResource
import com.checkin.app.checkin.data.resource.Resource.Companion.errorButLoadedCached
import com.checkin.app.checkin.data.resource.Resource.Companion.errorNotFoundCached
import com.checkin.app.checkin.data.resource.Resource.Companion.loading
import com.checkin.app.checkin.data.resource.Resource.Companion.success
import com.checkin.app.checkin.data.resource.Resource.Companion.successCached

// ResultType: Type for the Resource data
// RequestType: Type for the API response
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor() {
    private val mResult = MediatorLiveData<Resource<ResultType>>()
    protected var `val`: Any? = null

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>?) {
        val apiResponse = createCall()
        val useDb = shouldUseLocalDb() && dbSource != null
        if (useDb) {
            // we re-attach dbSource as a new source, it will dispatch its latest value quickly
            mResult.addSource(dbSource!!) { mResult.setValue(loading(it)) }
        }
        mResult.addSource(apiResponse) { response ->
            mResult.removeSource(apiResponse)
            if (useDb) mResult.removeSource(dbSource!!)
            if (response != null) {
                val resource = createResource(response)
                if (useDb && resource.status == Resource.Status.SUCCESS) saveResultAndReInit(resource) else postResultDirectly(resource, response.requestUrl)
                if (!response.isSuccessful) {
                    onFetchFailed(response)
                    if (useDb) {
                        mResult.addSource(dbSource!!) {
                            if (it == null || (it is List<*> && it.isEmpty())) mResult.value = errorNotFoundCached<ResultType>(resource.message)
                            else mResult.value = errorButLoadedCached(resource.message, it)
                        }
                    }
                }
            }
        }
    }

    @MainThread
    private fun saveResultAndReInit(resource: Resource<RequestType>) {
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                saveCallResult(resource.data)
                return null
            }

            override fun onPostExecute(param: Void?) {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                mResult.addSource(loadFromDb()!!) {
                    if (it != null) mResult.setValue(success(it))
                    else mResult.value = cloneResource(resource, null)
                }
            }
        }.execute()
    }

    // Called in case no Database interaction needed.
    @MainThread
    protected fun postResultDirectly(resource: Resource<RequestType>, url: String?) {
        val data = resource.data
        Log.e(TAG, "${resource.status.name} - $url")
        try {
            mResult.value = cloneResource(resource, data as? ResultType)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Invalid Resource Data type.")
        }
    }

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected open fun shouldFetch(data: ResultType?): Boolean = true

    protected open fun shouldUseLocalDb(): Boolean = false

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    // Called to get the cached data from the database
    @MainThread
    protected open fun loadFromDb(): LiveData<ResultType>? = null

    // Called to save the result of the API response into the database
    @WorkerThread
    protected open fun saveCallResult(data: RequestType?) {
    }

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected fun onFetchFailed(response: ApiResponse<RequestType>?) {
    }

    // returns a LiveData that represents the resource, implemented
    // in the base class.
    val asLiveData: LiveData<Resource<ResultType>>
        get() = mResult

    companion object {
        private val TAG = NetworkBoundResource::class.java.simpleName
    }

    init {
        mResult.value = loading(null)
        loadFromDb()?.let { dbSource ->
            mResult.addSource(dbSource) { data ->
                mResult.removeSource(dbSource)
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource)
                } else {
                    mResult.addSource(dbSource) {
                        if (it != null) mResult.setValue(successCached(it))
                        else mResult.value = errorNotFoundCached<ResultType>(null)
                    }
                }
            }
        } ?: fetchFromNetwork(null)
    }
}
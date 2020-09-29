package com.checkin.app.checkin.data.resource

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
import com.checkin.app.checkin.utility.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// ResultType: Type for the Resource data
// RequestType: Type for the API response
abstract class NetworkBoundResource<ResultType, RequestType> : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val mResult = MediatorLiveData<Resource<ResultType>>()

    // returns a LiveData that represents the resource, implemented
    // in the base class.
    val asLiveData: LiveData<Resource<ResultType>> = mResult

    init {
        fetchData()
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>?) {
        val apiResponse = createCall()!!
        if (dbSource != null) {
            // we re-attach dbSource as a new source, it will dispatch its latest value quickly
            mResult.addSource(dbSource) {
                mResult.removeSource(dbSource)
                mResult.value = loading(it)
            }
        }
        mResult.addSource(apiResponse) { response ->
            mResult.removeSource(apiResponse)
            if (response != null) {
                val resource = createResource(response)
                if (shouldUseLocalDb() && resource.status == Resource.Status.SUCCESS) saveResultAndReInit(resource)
                else postResultDirectly(resource, response.requestUrl)
                if (!response.isSuccessful) {
                    onFetchFailed(response)
                    if (dbSource != null) {
                        mResult.addSource(dbSource) {
                            if (it == null || (it is List<*> && it.isEmpty())) mResult.value = errorNotFoundCached<ResultType>(resource.message)
                            else mResult.value = errorButLoadedCached(resource.message, it)
                        }
                    }
                }
            }
        }
    }

    protected open fun fetchData() {
        mResult.value = loading(null)
        val dbSource = if (shouldUseLocalDb()) loadFromDb() else null
        if (dbSource != null) {
            mResult.addSource(dbSource) { data ->
                mResult.removeSource(dbSource)
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource)
                } else {
                    mResult.addSource(dbSource) {
                        if (it == null || (it is List<*> && it.isEmpty())) mResult.value = errorNotFoundCached<ResultType>(null)
                        else mResult.value = successCached(it)
                    }
                }
            }
        } else fetchFromNetwork(null)
    }

    @MainThread
    private fun saveResultAndReInit(resource: Resource<RequestType>) = runBlocking {
        launch { saveCallResult(resource.data) }.join()
        // we specially request a new live data,
        // otherwise we will get immediately last cached value,
        // which may not be updated with latest results received from network.
        val dbResponse = loadFromDb()
        if (dbResponse != null) mResult.addSource(dbResponse) {
            mResult.removeSource(dbResponse)
            if (it != null) mResult.value = transformResult(resource, success(it))
            else mResult.value = cloneResource(resource, null)
        } else mResult.value = transformResult(resource)
    }

    // Called in case no Database interaction needed.
    @MainThread
    protected fun postResultDirectly(resource: Resource<RequestType>, url: String?) {
        Log.d(TAG, "${resource.status.name} - $url")
        mResult.value = transformResult(resource)
    }

    /**
     * Transforms the network resource received at the end of lifecycle
     * By default, if [dbResult] is present returns that otherwise returns the network resource, [networkResult]
     */
    protected open fun transformResult(networkResult: Resource<RequestType>, dbResult: Resource<ResultType>? = null): Resource<ResultType> = kotlin.runCatching {
        dbResult ?: cloneResource(networkResult, networkResult.data as? ResultType)!!
    }.getOrElse {
        Utils.logErrors(TAG, it, "Invalid Resource Data type.")
        throw it
    }

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected open fun shouldFetch(data: ResultType?): Boolean = true

    protected open fun shouldUseLocalDb(): Boolean = false

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>?

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

    companion object {
        private val TAG = NetworkBoundResource::class.java.simpleName
    }
}
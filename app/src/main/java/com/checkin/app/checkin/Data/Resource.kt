package com.checkin.app.checkin.Data

import android.util.Log
import com.checkin.app.checkin.Utility.NoConnectivityException
import com.checkin.app.checkin.Utility.RequestCanceledException
import com.crashlytics.android.Crashlytics
import com.fasterxml.jackson.databind.JsonNode
import java.net.HttpURLConnection.*

// A generic class that describes data with a status.
class Resource<out T> private constructor(val status: Status, val data: T?, val message: String?, val errorBody: JsonNode?) {
    private var problemModel: ProblemModel? = null

    var isCached = false
        private set

    val problem: ProblemModel?
        get() {
            if (problemModel == null)
                problemModel = ProblemModel.fromResource(this)
            return problemModel
        }

    fun hasErrorBody(): Boolean {
        return errorBody != null
    }

    enum class Status {
        NO_REQUEST,
        SUCCESS,
        LOADING,
        ERROR_RESPONSE_INVALID,
        ERROR_DISCONNECTED,
        ERROR_NOT_FOUND,
        ERROR_INVALID_REQUEST,
        ERROR_FORBIDDEN,
        ERROR_UNAUTHORIZED,
        ERROR_CANCELLED,
        ERROR_NOT_ACCEPTABLE,
        ERROR_UNKNOWN
    }

    companion object {
        private val TAG = Resource::class.java.simpleName

        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null, null)

        fun <T> successCached(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null, null).apply { isCached = true }

        fun <T> error(status: Status, msg: String?, data: T?, errorBody: JsonNode?): Resource<T> = Resource(status, data, msg, errorBody)

        fun <T> error(msg: String?, data: T?, errorBody: JsonNode?): Resource<T> = error(Status.ERROR_UNKNOWN, msg, data, errorBody)

        fun <T> error(msg: String?, data: T?): Resource<T> = error(msg, data, null)

        fun <T> errorNotFound(msg: String?, errorBody: JsonNode?): Resource<T> = error(Status.ERROR_NOT_FOUND, msg, null, errorBody)

        fun <T> errorNotFound(msg: String?): Resource<T> = error(msg, null)

        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null, null)

        fun <T> noRequest(): Resource<T> = Resource(Status.NO_REQUEST, null, null, null)

        fun <T> createResource(apiResponse: ApiResponse<T>): Resource<T> {
            return when {
                apiResponse.isSuccessful -> success(apiResponse.data)
                apiResponse.errorThrowable != null -> when {
                    apiResponse.errorThrowable is RequestCanceledException -> error<T>(Status.ERROR_CANCELLED, null, null, null)
                    apiResponse.errorThrowable is NoConnectivityException -> error(Status.ERROR_DISCONNECTED, apiResponse.errorMessage, apiResponse.data, null)
                    else -> {
                        Log.e(TAG, apiResponse.errorMessage, apiResponse.errorThrowable)
                        Crashlytics.log(Log.ERROR, TAG, apiResponse.errorMessage)
                        Crashlytics.logException(apiResponse.errorThrowable)
                        error(Status.ERROR_UNKNOWN, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                    }
                }
                apiResponse.hasStatus(HTTP_NOT_FOUND) -> error(Status.ERROR_NOT_FOUND, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_BAD_REQUEST) -> error(Status.ERROR_INVALID_REQUEST, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_UNAUTHORIZED) -> error(Status.ERROR_UNAUTHORIZED, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_FORBIDDEN) -> error(Status.ERROR_FORBIDDEN, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_NOT_ACCEPTABLE) -> error(Status.ERROR_NOT_ACCEPTABLE, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                else -> error(Status.ERROR_UNKNOWN, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
            }
        }

        fun <X, T> cloneResource(resource: Resource<T>, data: X): Resource<X> = Resource(resource.status, data, resource.message, resource.errorBody).apply { isCached = resource.isCached }
    }
}

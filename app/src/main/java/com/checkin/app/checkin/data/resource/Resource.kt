package com.checkin.app.checkin.data.resource

import android.util.Log
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.misc.exceptions.NetworkIssueException
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import com.checkin.app.checkin.misc.exceptions.RequestCanceledException
import com.checkin.app.checkin.utility.Utils
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

    fun hasErrorBody(): Boolean = errorBody != null

    val isSuccess: Boolean = status == Status.SUCCESS || status == Status.ERROR_BUT_LOADED_CACHED

    @get:JvmName("isInError")
    val inError: Boolean = status != Status.NO_REQUEST && !isSuccess && status != Status.LOADING && status != Status.ERROR_CANCELLED

    val mayLoad: Boolean = isSuccess || status == Status.LOADING

    enum class Status {
        NO_REQUEST,
        SUCCESS,
        LOADING,
        ERROR_RESPONSE_INVALID,
        ERROR_DISCONNECTED,
        ERROR_NOT_FOUND,
        ERROR_NOT_FOUND_CACHED,
        ERROR_BUT_LOADED_CACHED,
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

        fun <T> errorNotFound(msg: String?): Resource<T> = errorNotFound(msg, null)

        fun <T> errorNotFoundCached(msg: String?) = error(Status.ERROR_NOT_FOUND_CACHED, msg, null, null)

        fun <T> errorButLoadedCached(msg: String?, data: T?) = error(Status.ERROR_BUT_LOADED_CACHED, msg, data, null)

        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null, null)

        fun <T> noRequest(): Resource<T> = Resource(Status.NO_REQUEST, null, null, null)

        fun <T> error(errorThrowable: Throwable, errorMessage: String?, data: T?, errorData: JsonNode?): Resource<T> = when (errorThrowable) {
            is RequestCanceledException -> error<T>(Status.ERROR_CANCELLED, null, null, null)
            is NoConnectivityException, is NetworkIssueException -> error(Status.ERROR_DISCONNECTED, errorMessage, data, null)
            else -> {
                Utils.logErrors(TAG, errorThrowable, errorMessage)
                error(Status.ERROR_UNKNOWN, errorMessage, data, errorData)
            }
        }

        fun <T> error(errorThrowable: Throwable): Resource<T> = error(errorThrowable, null, null, null)

        fun <T> createResource(apiResponse: ApiResponse<T>): Resource<T> {
            return when {
                apiResponse.isSuccessful -> success(apiResponse.data)
                apiResponse.errorThrowable != null -> error(apiResponse.errorThrowable, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_NOT_FOUND) -> error(Status.ERROR_NOT_FOUND, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_BAD_REQUEST) -> error(Status.ERROR_INVALID_REQUEST, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_UNAUTHORIZED) -> error(Status.ERROR_UNAUTHORIZED, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_FORBIDDEN) -> error(Status.ERROR_FORBIDDEN, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                apiResponse.hasStatus(HTTP_NOT_ACCEPTABLE) -> error(Status.ERROR_NOT_ACCEPTABLE, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                else -> {
                    Crashlytics.log(Log.ERROR, TAG, apiResponse.errorMessage)
                    Crashlytics.log(Log.ERROR, TAG, apiResponse.errorData?.toString())
                    error(Status.ERROR_UNKNOWN, apiResponse.errorMessage, apiResponse.data, apiResponse.errorData)
                }
            }
        }

        fun <X, T> cloneResource(resource: Resource<T>?, data: X?): Resource<X>? = resource?.let { Resource(it.status, data, it.message, it.errorBody).apply { isCached = it.isCached } }
    }
}

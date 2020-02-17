package com.checkin.app.checkin.data.network

import android.util.Log
import com.checkin.app.checkin.data.Converters.getJsonNode
import com.checkin.app.checkin.misc.exceptions.JacksonProcessingException
import com.checkin.app.checkin.misc.exceptions.NetworkIssueException
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import com.checkin.app.checkin.misc.exceptions.RequestCanceledException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

class ApiResponse<T>(
        private var mResponse: Response<T>?,
        val statusCode: Int,
        val data: T? = null,
        val errorThrowable: Throwable? = null
) {
    var requestUrl: String? = null

    private val mErrorMsg: String? by lazy {
        mResponse?.errorBody()?.let {
            try {
                it.string()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } ?: errorThrowable?.message
    }
    val errorData: JsonNode? by lazy {
        if (mResponse != null) {
            getJsonNode(mErrorMsg ?: return@lazy null)
        } else null
    }

    constructor(error: Throwable) : this(null, HttpURLConnection.HTTP_CLIENT_TIMEOUT, errorThrowable = getErrorThrowable(error))

    constructor(response: Response<T>) : this(response, response.code(), response.body())

    val isSuccessful: Boolean
        get() = mResponse != null && mResponse!!.isSuccessful

    val errorMessage: String?
        get() =
            errorData?.let { data ->
                Log.e("APIResponse", "Error data: $data")
                when {
                    data.isObject -> {
                        when {
                            data.has("detail") -> {
                                Log.d("APIResponse", "Detail")
                                data["detail"].asText()
                            }
                            data["errors"]?.isArray == true -> {
                                Log.d("APIResponse", "Errors")
                                data["errors"][0].asText()
                            }
                            data.has("title") -> data["title"].asText()
                            else -> null
                        }
                    }
                    data.isArray -> {
                        Log.d("APIResponse", "Array")
                        data[0].asText()
                    }
                    else -> null
                }
            } ?: mErrorMsg

    fun hasStatus(statusCode: Int): Boolean {
        return this.statusCode == statusCode
    }

    companion object {
        fun getErrorThrowable(error: Throwable): Throwable = if (error !is NoConnectivityException && error is IOException) {
            if ("Canceled" == error.message) RequestCanceledException(error) else NetworkIssueException(error)
        } else {
            (error as? JsonProcessingException)?.let { JacksonProcessingException(it) } ?: error
        }
    }
}
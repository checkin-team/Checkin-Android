package com.checkin.app.checkin.payment.services.paytm

import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.checkin.app.checkin.misc.exceptions.NetworkIssueException
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import net.one97.paytm.nativesdk.instruments.netbanking.modal.NBResponse
import net.one97.paytm.nativesdk.paymethods.datasource.PaymentMethodDataSource
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PaytmSuspendingDataSource<Result, PaytmData>(val continuation: Continuation<Result>, val convert: (PaytmData) -> Result?) : PaymentMethodDataSource.Callback<PaytmData> {
    override fun onErrorResponse(error: VolleyError?, errorInfo: PaytmData?) {
        val throwable = when {
            error != null -> {
                when (error) {
                    is NoConnectionError -> NoConnectivityException()
                    is NetworkError, is TimeoutError -> NetworkIssueException(error)
                    else -> error
                }

            }
            errorInfo != null -> {
                when (errorInfo) {
                    is NBResponse -> errorInfo.body.resultInfo.let { PaytmDataException(it?.resultCode, it?.resultMsg, it?.resultStatus) }
                    else -> PaytmDataException(null, null, null)
                }
            }
            else -> IllegalStateException("Both 'error' and 'errorInfo' are null!")
        }
        continuation.resumeWithException(throwable)
    }

    override fun onResponse(response: PaytmData?) {
        if (response != null) {
            convert(response)?.let {
                continuation.resume(it)
            } ?: onErrorResponse(null, response)
        }
    }
}
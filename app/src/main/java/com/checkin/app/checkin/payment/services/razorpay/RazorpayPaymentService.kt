package com.checkin.app.checkin.payment.services.razorpay

import android.content.Context
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.models.razorpay.RazorpayPaymentMethod
import com.checkin.app.checkin.payment.services.IPaymentService
import com.checkin.app.checkin.payment.services.Transaction
import com.fasterxml.jackson.core.type.TypeReference
import com.razorpay.BaseRazorpay
import com.razorpay.Razorpay
import com.razorpay.RzpUpiSupportedAppsCallback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object RazorpayPaymentService : IPaymentService {
    private var paymentMethods: RazorpayPaymentMethod? = null

    override suspend fun getNetBankingOptions(transaction: Transaction): List<NetBankingPaymentOptionModel> {
        return getPaymentMethods(transaction).netbanking.map {
            NetBankingPaymentOptionModel(it.key, it.value, 0)
        }
    }

    override suspend fun getUPIAppOptions(context: Context): List<UPIPushPaymentOptionModel> = suspendCoroutine { continuation ->
        val callback = RzpUpiSupportedAppsCallback {
            val data = it.map {
                UPIPushPaymentOptionModel(it.appName, it.iconBase64, it.packageName)
            }
            continuation.resume(data)
        }
        Razorpay.getAppsWhichSupportUpi(context, callback)
    }

    override suspend fun getPaymentMethods(transaction: Transaction): RazorpayPaymentMethod = suspendCoroutine { continuation ->
        paymentMethods?.also {
            continuation.resume(it)
            return@suspendCoroutine
        }
        val callback = object : BaseRazorpay.PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(result: String?) {
                if (result != null)
                    kotlin.runCatching {
                        Converters.getObjectFromJson(result, object : TypeReference<RazorpayPaymentMethod>() {})
                    }.onFailure { continuation.resumeWithException(it) }.onSuccess {
                        if (it != null) {
                            paymentMethods = it
                            continuation.resume(it)
                        }
                    }
            }

            override fun onError(error: String?) {
                continuation.resumeWithException(RazorpayDataException(error))
            }
        }
        transaction.getPaymentMethods(callback)
    }
}
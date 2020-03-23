package com.checkin.app.checkin.payment.services.razorpay

import android.content.Context
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.payment.models.CardPaymentOptionModel
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.models.razorpay.RazorpayPaymentMethod
import com.checkin.app.checkin.payment.services.IPaymentService
import com.checkin.app.checkin.payment.services.PaymentProvider
import com.checkin.app.checkin.payment.services.PaymentServiceLocator
import com.fasterxml.jackson.core.type.TypeReference
import com.razorpay.BaseRazorpay
import com.razorpay.Razorpay
import com.razorpay.RzpUpiSupportedAppsCallback
import com.razorpay.ValidateVpaCallback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object RazorpayPaymentService : IPaymentService {
    private var paymentMethods: RazorpayPaymentMethod? = null
    private val provider: PaymentProvider
        get() = PaymentServiceLocator.getInstance().paymentProvider

    override suspend fun getNetBankingOptions(): List<NetBankingPaymentOptionModel> {
        return paymentMethods?.netbanking?.map {
            NetBankingPaymentOptionModel(it.key, it.value, 0)
        } ?: emptyList()
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

    override suspend fun getPaymentMethods(): RazorpayPaymentMethod = suspendCoroutine { continuation ->
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
        provider.getPaymentMethods(callback)
    }

    override suspend fun isValidVpa(vpa: String): Boolean = suspendCoroutine { continuation ->
        val callback = object : ValidateVpaCallback {
            override fun onFailure() {
                // By default, if call fails resume execution assuming valid VPA
                continuation.resume(false)
            }

            override fun onResponse(isValid: Boolean) {
                continuation.resume(isValid)
            }
        }
        provider.isValidVpa(vpa, callback)
    }

    override suspend fun getCardNetwork(cardNumber: String): CardPaymentOptionModel.CARD_PROVIDER? = when (provider.getCardNetwork(cardNumber)) {
        "visa" -> CardPaymentOptionModel.CARD_PROVIDER.VISA
        "mastercard" -> CardPaymentOptionModel.CARD_PROVIDER.MASTER_CARD
        "maestro", "maestro16" -> CardPaymentOptionModel.CARD_PROVIDER.MAESTRO
        "rupay" -> CardPaymentOptionModel.CARD_PROVIDER.RUPAY
        "amex" -> CardPaymentOptionModel.CARD_PROVIDER.AMEX
        else -> null
    }

    override suspend fun isValidCardNumber(cardNumber: String): Boolean = provider.isValidCardNumber(cardNumber)
}
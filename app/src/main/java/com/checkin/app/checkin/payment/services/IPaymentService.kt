package com.checkin.app.checkin.payment.services

import android.content.Context
import com.checkin.app.checkin.payment.models.CardPaymentOptionModel
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.PaymentMethods
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel

interface IPaymentService {
    suspend fun getPaymentMethods(): PaymentMethods
    suspend fun getNetBankingOptions(): List<NetBankingPaymentOptionModel>
    suspend fun getUPIAppOptions(context: Context): List<UPIPushPaymentOptionModel>
    suspend fun isValidVpa(vpa: String): Boolean
    suspend fun getCardNetwork(cardNumber: String): CardPaymentOptionModel.CARD_PROVIDER?
    suspend fun isValidCardNumber(cardNumber: String): Boolean

}
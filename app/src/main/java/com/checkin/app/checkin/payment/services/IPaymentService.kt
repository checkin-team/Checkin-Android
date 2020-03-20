package com.checkin.app.checkin.payment.services

import android.content.Context
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.PaymentMethods
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel

interface IPaymentService {
    suspend fun getPaymentMethods(transaction: Transaction): PaymentMethods
    suspend fun getNetBankingOptions(transaction: Transaction): List<NetBankingPaymentOptionModel>
    suspend fun getUPIAppOptions(context: Context): List<UPIPushPaymentOptionModel>
}
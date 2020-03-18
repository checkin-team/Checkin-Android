package com.checkin.app.checkin.payment.services

import android.content.Context
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel

interface IPaymentService {
    suspend fun getNetBankingOptions(context: Context): List<NetBankingPaymentOptionModel>
    suspend fun getUPIAppOptions(context: Context): List<UPIPushPaymentOptionModel>
}
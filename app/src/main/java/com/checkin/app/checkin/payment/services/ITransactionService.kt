package com.checkin.app.checkin.payment.services

import android.content.Context
import android.webkit.WebView
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.NewTransactionModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel

interface ITransactionService {
    val transaction: Transaction

    fun initialize(txnToken: String, amount: Double, merchantId: String, orderId: String) {}
    fun initialize(webView: WebView, txnData: NewTransactionModel) {}
    fun payByUPICollect(data: UPICollectPaymentOptionModel)
    fun payByUPIPush(data: UPIPushPaymentOptionModel)
    fun confirmUPIPushPayment(context: Context)
    fun payByNetBanking(context: Context, data: NetBankingPaymentOptionModel)
}
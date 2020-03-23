package com.checkin.app.checkin.payment.services

import android.webkit.WebView
import com.checkin.app.checkin.payment.models.*

interface ITransactionService {
    fun initialize(txnToken: String, amount: Double, merchantId: String, orderId: String) {}
    fun initialize(webView: WebView, txnData: NewTransactionModel) {}
    fun payByUPICollect(data: UPICollectPaymentOptionModel)
    fun payByUPIPush(data: UPIPushPaymentOptionModel)
    fun payByNetBanking(data: NetBankingPaymentOptionModel)
    fun payByCard(data: CardPaymentOptionModel)
}
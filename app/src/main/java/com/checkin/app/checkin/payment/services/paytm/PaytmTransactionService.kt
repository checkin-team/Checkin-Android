package com.checkin.app.checkin.payment.services.paytm

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.checkin.app.checkin.misc.exceptions.NetworkIssueException
import com.checkin.app.checkin.payment.listeners.TransactionListener
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.TransactionResponseModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.services.ITransactionService
import com.checkin.app.checkin.utility.Constants
import net.one97.paytm.nativesdk.PaytmSDK
import net.one97.paytm.nativesdk.Utils.Server
import net.one97.paytm.nativesdk.app.PaytmSDKCallbackListener
import net.one97.paytm.nativesdk.app.UpiPushCallbackListener
import net.one97.paytm.nativesdk.common.model.CustomVolleyError

class PaytmTransactionService(private val context: Context, private val listener: TransactionListener) : ITransactionService, PaytmSDKCallbackListener, UpiPushCallbackListener {
    private val paytmSdk by lazy { PaytmSDK.getInstance() }
    private val paytmHelper by lazy { PaytmSDK.getPaymentsHelper() }

    override fun initialize(txnToken: String, amount: Double, merchantId: String, orderId: String) {
        paytmSdk.initialize(context, txnToken, false, amount, merchantId, orderId, this)
        paytmSdk.setEnablePaytmAssist(false)
        paytmSdk.setIsAppInvoke(false)
        paytmSdk.setUpiPushCallBackListener(this)
        PaytmSDK.setServer(if (Constants.IS_RELEASE_BUILD()) Server.PRODUCTION else Server.STAGING)
    }

    override fun payByUPICollect(data: UPICollectPaymentOptionModel) {
        paytmHelper.doUpiTransaction(context, data.vpa)
    }

    override fun payByUPIPush(data: UPIPushPaymentOptionModel) {
        paytmHelper.doUpiTransaction(context, data.appName, data.activityInfo)
    }

    override fun confirmUPIPushPayment(context: Context) {
        paytmHelper.makeUPITransactionStatusRequest(context)
    }

    override fun payByNetBanking(context: Context, data: NetBankingPaymentOptionModel) {
        paytmHelper.doNBTransaction(context, "NET_BANKING", data.bankCode)
    }

    override fun onTransactionResponse(txnResponse: Bundle?) {
        println("TXNRESPONSE ==== $txnResponse")
        listener.onTransactionResponse(TransactionResponseModel(""))
    }

    override fun onGenericError(errorCode: Int, errorMsg: String?) {
        listener.onTransactionError(PaytmTransactionException(errorCode, errorMsg))
    }

    override fun networkError() {
        listener.onTransactionError(NetworkIssueException(null))
    }

    override fun onTransactionCancel(reason: String?) {
        listener.onTransactionCancel(reason)
    }

    override fun onBackPressedCancelTransaction() {
        listener.onTransactionCancel("User pressed back")
    }

    override fun onUpiPushTxnFailure(p0: String?, p1: CustomVolleyError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openMPINScreen(p0: Context?, p1: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkBalance(p0: Activity?, p1: String?, p2: Int, p3: UpiPushCallbackListener.CheckBalanceListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

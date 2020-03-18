package com.checkin.app.checkin.payment.services.paytm

import android.content.Context
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.services.IPaymentService
import net.one97.paytm.nativesdk.PaytmSDK
import net.one97.paytm.nativesdk.instruments.netbanking.modal.NBResponse
import kotlin.coroutines.suspendCoroutine

object PaytmPaymentService : IPaymentService {
    private val paytmHelper by lazy { PaytmSDK.getPaymentsHelper() }

    override suspend fun getNetBankingOptions(context: Context): List<NetBankingPaymentOptionModel> = suspendCoroutine { continuation ->
        val callback = PaytmSuspendingDataSource<List<NetBankingPaymentOptionModel>, NBResponse>(continuation) {
            it.body?.nbPayOption?.let {
                it.payChannelOptions?.map { option ->
                    NetBankingPaymentOptionModel(option.channelCode, option.channelName, option.iconUrl)
                } ?: emptyList()
            }
        }
        paytmHelper.getNBList(context, callback)
    }

    override suspend fun getUPIAppOptions(context: Context): List<UPIPushPaymentOptionModel> {
        return paytmHelper.getUpiAppsInstalled(context).map {
            UPIPushPaymentOptionModel(it.appName, it.drawable, it.resolveInfo.activityInfo)
        }
    }
}

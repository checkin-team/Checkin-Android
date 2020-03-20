package com.checkin.app.checkin.payment

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.NewRazorpayTransactionModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.services.PaymentServiceLocator
import com.checkin.app.checkin.payment.services.Transaction
import com.checkin.app.checkin.utility.SingletonHolder
import io.objectbox.android.ObjectBoxLiveData

class PaymentRepository private constructor(val context: Context) : BaseRepository() {
    private val webService: WebApiService = ApiClient.getApiService(context)
    private val boxUpiCollectOption = AppDatabase.boxFor<UPICollectPaymentOptionModel>()
    private val boxUpiPushOption = AppDatabase.boxFor<UPIPushPaymentOptionModel>()
    private val paymentService = PaymentServiceLocator.paymentService

    fun getNetBankingOptions(transaction: Transaction): LiveData<Resource<List<NetBankingPaymentOptionModel>>> = liveData {
        kotlin.runCatching {
            paymentService.getNetBankingOptions(transaction).map {
                val iconRes = when (it.bankCode) {
                    "SBIN" -> R.drawable.ic_payment_netbanking_sbi
                    "UTIB" -> R.drawable.ic_payment_netbanking_axis
                    "KKBK" -> R.drawable.ic_payment_netbanking_kotak
                    else -> 0
                }
                it.copy(iconRes = iconRes)
            }
        }.onFailure { emit(Resource.error(it)) }.onSuccess { emit(Resource.success(it)) }
    }

    val UPIAppOptions: LiveData<Resource<List<UPIPushPaymentOptionModel>>>
        get() = liveData {
            kotlin.runCatching {
                paymentService.getUPIAppOptions(context)
            }.onFailure { emit(Resource.error(it)) }.onSuccess { emit(Resource.success(it)) }
        }

    val UPIIdOptions: LiveData<Resource<List<UPICollectPaymentOptionModel>>>
        get() = object : NetworkBoundResource<List<UPICollectPaymentOptionModel>, List<UPICollectPaymentOptionModel>>() {
            override fun shouldFetch(data: List<UPICollectPaymentOptionModel>?): Boolean = false

            override fun shouldUseLocalDb(): Boolean = true

            override fun createCall(): LiveData<ApiResponse<List<UPICollectPaymentOptionModel>>>? = null

            override fun loadFromDb(): LiveData<List<UPICollectPaymentOptionModel>>? = ObjectBoxLiveData(boxUpiCollectOption.query().build())
        }.asLiveData

    fun createNewTransaction(sessionId: Long): LiveData<Resource<NewRazorpayTransactionModel>> {
        return object : NetworkBoundResource<NewRazorpayTransactionModel, NewRazorpayTransactionModel>() {
            override fun createCall(): LiveData<ApiResponse<NewRazorpayTransactionModel>>? {
                return RetrofitLiveData(webService.postNewRazorpayTransaction(sessionId))
            }
        }.asLiveData
    }

    companion object : SingletonHolder<PaymentRepository, Application>({ PaymentRepository(it.applicationContext) })
}
package com.checkin.app.checkin.payment

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel
import com.checkin.app.checkin.payment.models.NewPaytmTransactionModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.payment.services.IPaymentService
import com.checkin.app.checkin.payment.services.paytm.PaytmPaymentService
import com.checkin.app.checkin.utility.SingletonHolder
import io.objectbox.android.ObjectBoxLiveData

class PaymentRepository private constructor(val context: Context) : BaseRepository() {
    private val webService: WebApiService = ApiClient.getApiService(context)
    private val boxUpiCollectOption = AppDatabase.getUPICollectPaymentOptionModel()
    private val boxUpiPushOption = AppDatabase.getUPIPushPaymentOptionModel()
    private val paymentService: IPaymentService = PaytmPaymentService

    val netBankingOptions: LiveData<Resource<List<NetBankingPaymentOptionModel>>>
        get() = liveData {
            kotlin.runCatching {
                paymentService.getNetBankingOptions(context)
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

    fun createNewTransaction(sessionId: Long): LiveData<Resource<NewPaytmTransactionModel>> {
        return object : NetworkBoundResource<NewPaytmTransactionModel, NewPaytmTransactionModel>() {
            override fun createCall(): LiveData<ApiResponse<NewPaytmTransactionModel>>? {
                return RetrofitLiveData(webService.postNewPaytmTransaction(sessionId))
            }
        }.asLiveData
    }

    companion object : SingletonHolder<PaymentRepository, Application>({ PaymentRepository(it.applicationContext) })
}
package com.checkin.app.checkin.payment

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.dbStore
import com.checkin.app.checkin.data.network.ApiClient
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.payment.models.*
import com.checkin.app.checkin.payment.services.PaymentServiceLocator
import com.checkin.app.checkin.utility.SingletonHolder
import com.checkin.app.checkin.utility.pass
import com.fasterxml.jackson.databind.node.ObjectNode
import io.objectbox.android.ObjectBoxLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class PaymentRepository private constructor(val context: Context) : BaseRepository() {
    private val webService: WebApiService = ApiClient.getApiService(context)
    private val boxUpiCollectOption by dbStore<UPICollectPaymentOptionModel>()
    private val boxUpiPushOption by dbStore<UPIPushPaymentOptionModel>()
    private val boxCardOption by dbStore<CardPaymentOptionModel>()
    private val paymentService by lazy {
        PaymentServiceLocator.getInstance().paymentService
    }

    private lateinit var cachedPaymentMethods: LiveData<Resource<PaymentMethods>>

    fun getPaymentMethods(): LiveData<Resource<PaymentMethods>> {
        if (!::cachedPaymentMethods.isInitialized)
            cachedPaymentMethods = liveData {
                kotlin.runCatching {
                    paymentService.getPaymentMethods()
                }
                        .onFailure { emit(Resource.error<PaymentMethods>(it)) }
                        .onSuccess { emit(Resource.success(it)) }
            }
        return cachedPaymentMethods
    }

    val netBankingOptions: LiveData<Resource<List<NetBankingPaymentOptionModel>>>
        get() = liveData {
            kotlin.runCatching {
                paymentService.getNetBankingOptions().map {
                    val iconRes = when (it.bankCode) {
                        "SBIN" -> R.drawable.ic_payment_netbanking_sbi
                        "UTIB" -> R.drawable.ic_payment_netbanking_axis
                        "KKBK" -> R.drawable.ic_payment_netbanking_kotak
                        else -> 0
                    }
                    it.copy(iconRes = iconRes)
                }
            }
                    .onFailure { emit(Resource.error<List<NetBankingPaymentOptionModel>>(it)) }
                    .onSuccess { emit(Resource.success(it)) }
        }

    val UPIAppOptions: LiveData<Resource<List<UPIPushPaymentOptionModel>>>
        get() = liveData {
            kotlin.runCatching {
                paymentService.getUPIAppOptions(context)
            }
                    .onFailure { emit(Resource.error<List<UPIPushPaymentOptionModel>>(it)) }
                    .onSuccess { emit(Resource.success(it)) }
        }

    val UPIIdOptions: LiveData<Resource<List<UPICollectPaymentOptionModel>>>
        get() = object : NetworkBoundResource<List<UPICollectPaymentOptionModel>, List<UPICollectPaymentOptionModel>>() {
            override fun shouldFetch(data: List<UPICollectPaymentOptionModel>?): Boolean = false

            override fun shouldUseLocalDb(): Boolean = true

            override fun createCall(): LiveData<ApiResponse<List<UPICollectPaymentOptionModel>>>? = null

            override fun loadFromDb(): LiveData<List<UPICollectPaymentOptionModel>>? = ObjectBoxLiveData(boxUpiCollectOption.query().build())
        }.asLiveData

    val cardOptions: LiveData<Resource<List<CardPaymentOptionModel>>>
        get() = object : NetworkBoundResource<List<CardPaymentOptionModel>, List<CardPaymentOptionModel>>() {
            override fun shouldFetch(data: List<CardPaymentOptionModel>?): Boolean = false

            override fun shouldUseLocalDb(): Boolean = true

            override fun createCall(): LiveData<ApiResponse<List<CardPaymentOptionModel>>>? = null

            override fun loadFromDb(): LiveData<List<CardPaymentOptionModel>>? = ObjectBoxLiveData(boxCardOption.query().build())
        }.asLiveData

    fun createNewTransaction(sessionId: Long): LiveData<Resource<NewRazorpayTransactionModel>> {
        return object : NetworkBoundResource<NewRazorpayTransactionModel, NewRazorpayTransactionModel>() {
            override fun createCall(): LiveData<ApiResponse<NewRazorpayTransactionModel>>? {
                return RetrofitLiveData(webService.postNewRazorpayTransaction(sessionId))
            }
        }.asLiveData
    }

    fun postTransactionResponse(data: TransactionResponseModel): LiveData<Resource<ObjectNode>> {
        return object : NetworkBoundResource<ObjectNode, ObjectNode>() {
            override fun createCall(): LiveData<ApiResponse<ObjectNode>>? {
                return RetrofitLiveData(webService.postRazorpayCallback(data as RazorpayTxnResponseModel))
            }
        }.asLiveData
    }

    suspend fun savePaymentOption(paymentOptionModel: PaymentOptionModel) = withContext(Dispatchers.IO) {
        val currentTime = Calendar.getInstance().time
        when (paymentOptionModel) {
            is UPIPushPaymentOptionModel -> {
                val data = boxUpiPushOption.getByAppName(paymentOptionModel.appName)
                        ?: paymentOptionModel
                data.lastUsed = currentTime
                boxUpiPushOption.put(data)
            }
            is UPICollectPaymentOptionModel -> {
                val data = boxUpiCollectOption.getByVpa(paymentOptionModel.vpa)
                        ?: paymentOptionModel
                data.lastUsed = currentTime
                boxUpiCollectOption.put(data)
            }
            is CardPaymentOptionModel -> {
                val data = boxCardOption.getByCardNumber(paymentOptionModel.cardNumber)
                        ?: paymentOptionModel
                data.lastUsed = currentTime
                boxCardOption.put(data)
            }
            else -> pass
        }
    }

    suspend fun isValidVpa(vpa: String) = paymentService.isValidVpa(vpa)

    suspend fun isValidCard(cardNumber: String) = paymentService.isValidCardNumber(cardNumber)

    suspend fun getCardNetwork(cardNumber: String) = paymentService.getCardNetwork(cardNumber)

    companion object : SingletonHolder<PaymentRepository, Application>({ PaymentRepository(it.applicationContext) })
}
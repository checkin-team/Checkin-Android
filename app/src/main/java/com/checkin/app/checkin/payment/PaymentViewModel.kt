package com.checkin.app.checkin.payment

import android.app.Application
import androidx.lifecycle.*
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.payment.models.*
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@UseExperimental(ExperimentalCoroutinesApi::class)
class PaymentViewModel(application: Application) : BaseViewModel(application) {
    private val repository = PaymentRepository.getInstance(application)

    lateinit var transactionData: NewTransactionModel
        private set

    private val payRequestChannel = ConflatedBroadcastChannel<PaymentRequest>()
    private val mPaymentMethods = createNetworkLiveData<PaymentMethods>()
    private val mUPIPushOptions = createNetworkLiveData<List<UPIPushPaymentOptionModel>>()
    private val mUPICollectOptions = createNetworkLiveData<List<UPICollectPaymentOptionModel>>()
    private val mNetBankingOptions = createNetworkLiveData<List<NetBankingPaymentOptionModel>>()
    private val mCardOptions = createNetworkLiveData<List<CardPaymentOptionModel>>()
    private val mAllOptions = MutableLiveData<List<PaymentOptionModel>>()
    private val mPayCallback = createNetworkLiveData<ObjectNode>()

    private val paymentCallback = mPayCallback.asFlow()

    val upiOptions: LiveData<Resource<List<UPIPaymentOptionModel>>> = MediatorLiveData<Resource<List<UPIPaymentOptionModel>>>().apply {
        addSource(mUPIPushOptions) {
            it?.let { listResource ->
                if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                    value = value?.data?.filterIsInstance<UPICollectPaymentOptionModel>()?.run { Resource.success(listResource.data + this) }
                            ?: listResource
            }
        }
        addSource(mUPICollectOptions) {
            it?.let { listResource ->
                if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                    value = value?.data?.filterIsInstance<UPIPushPaymentOptionModel>()?.run { Resource.success(this + listResource.data) }
                            ?: listResource
            }
        }
    }
    val netBankingOptions: LiveData<Resource<List<NetBankingPaymentOptionModel>>> = Transformations.map(mNetBankingOptions) {
        it?.data?.filter { it.iconRes != 0 }?.let { data ->
            Resource.cloneResource(it, data)
        } ?: it
    }
    val cardOptions: LiveData<Resource<List<CardPaymentOptionModel>>> = mCardOptions
    val recentlyUsedOptions: LiveData<List<PaymentOptionModel>> = Transformations.map(mAllOptions) {
        it?.distinct()?.sortedByDescending { it.lastUsed }
    }

    init {
        mNetBankingOptions.addSource(mPaymentMethods) {
            if (it?.status == Resource.Status.SUCCESS) fetchNetBankingOptions()
        }
    }

    fun init(transactionModel: NewTransactionModel) {
        transactionData = transactionModel
    }

    fun fetchUPIOptions() {
        mUPIPushOptions.addSource(repository.UPIAppOptions) {
            mUPIPushOptions.value = it
            it?.data?.also {
                val savedList = it.filter { it.lastUsed != null }
                mAllOptions.value = mAllOptions.value?.run { this + savedList } ?: savedList
            }
        }
        mUPICollectOptions.addSource(repository.UPIIdOptions) {
            mUPICollectOptions.value = it
            it?.data?.also {
                mAllOptions.value = mAllOptions.value?.run { this + it } ?: it
            }
        }
    }

    private fun fetchNetBankingOptions() {
        mNetBankingOptions.addSource(repository.netBankingOptions, mNetBankingOptions::setValue)
    }

    fun fetchPaymentMethods() {
        if (mPaymentMethods.value?.status == Resource.Status.SUCCESS) return

        mPaymentMethods.addSource(repository.getPaymentMethods(), mPaymentMethods::setValue)
    }

    fun fetchCardOptions() {
        mCardOptions.addSource(repository.cardOptions) {
            mCardOptions.value = it
            it?.data?.also {
                mAllOptions.value = mAllOptions.value?.run { this + it } ?: it
            }
        }
    }

    fun payUsing(paymentOptionModel: PaymentOptionModel, saveOption: Boolean = true) {
        payRequestChannel.offer(PaymentRequest(paymentOptionModel, saveOption))
    }

    fun onRequestPay(action: (PaymentOptionModel) -> Unit) = viewModelScope.launch {
        payRequestChannel.openSubscription().consumeEach { action(it.optionModel) }
    }

    fun onPaymentCallback(action: (Resource<ObjectNode>) -> Unit) = viewModelScope.launch {
        paymentCallback.collect { if (it != null) action(it) }
    }

    fun callPaymentCallback(data: TransactionResponseModel) {
        mPayCallback.addSource(repository.postTransactionResponse(data), mPayCallback::setValue)
    }

    fun savePaymentOption() {
        val lastRequest = payRequestChannel.valueOrNull
        if (lastRequest?.saveOption == true) {
            viewModelScope.launch {
                repository.savePaymentOption(lastRequest.optionModel)
            }
        }
    }

    suspend fun isValidVpa(vpa: String): Boolean = repository.isValidVpa(vpa)

    fun isValidCard(cardNumber: String): Boolean = runBlocking {
        repository.isValidCard(cardNumber)
    }

    fun getCardNetwork(cardNumber: String): CardPaymentOptionModel.CARD_PROVIDER? = runBlocking {
        repository.getCardNetwork(cardNumber)
    }

    override fun updateResults() {

    }
}

class PaymentRequest(val optionModel: PaymentOptionModel, val saveOption: Boolean)
package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Waiter.WaiterRepository
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.CheckoutStatusModel
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.utility.indexOfFirstOrNull
import java.util.*

class ManagerASLiveTablesViewModel(application: Application) : BaseViewModel(application) {
    private val mManagerRepository: ManagerRepository = ManagerRepository.getInstance(application)
    private val mWaiterRepository: WaiterRepository = WaiterRepository.getInstance(application)

    private val mTablesData = createNetworkLiveData<List<RestaurantTableModel>>()
    private val mCheckoutData = createNetworkLiveData<CheckoutStatusModel>()
    private val mQrResult = createNetworkLiveData<QRResultModel>()

    var shopPk: Long = 0

    val sessionInitiated: LiveData<Resource<QRResultModel>> = mQrResult

    val checkoutData: LiveData<Resource<CheckoutStatusModel>> = mCheckoutData

    val activeTables: LiveData<Resource<List<RestaurantTableModel>>> = Transformations.map(mTablesData) { input ->
        if (input?.data == null || input.status !== Resource.Status.SUCCESS) return@map input
        val result: MutableList<RestaurantTableModel> = ArrayList()
        for (i in input.data.indices) {
            val tableModel = input.data[i]
            if (tableModel.tableSession != null) result.add(tableModel)
        }
        result.sortWith(RestaurantTableModel.sortComparator)
        Resource.cloneResource(input, result)
    }

    val inactiveTables: LiveData<Resource<List<RestaurantTableModel>>> = Transformations.map(mTablesData) {
        it?.data?.let { list ->
            val data = list.filter { it.tableSession == null }
            Resource.cloneResource(it, data)
        } ?: it
    }

    fun markSessionDone(sessionId: Long) {
        val data = Converters.objectMapper.createObjectNode()
        data.put("payment_mode", "csh")
        mCheckoutData.addSource(mManagerRepository.manageSessionCheckout(sessionId), mCheckoutData::setValue)
    }

    fun fetchActiveTables(restaurantId: Long) {
        shopPk = restaurantId
        mTablesData.addSource(mWaiterRepository.getShopTables(restaurantId), mTablesData::setValue)
    }

    fun getTablePositionWithPk(sessionPk: Long): Int = mTablesData.value?.data?.indexOfFirst { it.sessionPk == sessionPk }
            ?: -1

    fun getTableWithPosition(position: Int): RestaurantTableModel? {
        val resource = mTablesData.value
        if (resource?.data == null) return null
        return if (position >= resource.data.size) null else resource.data[position]
    }

    fun addRestaurantTable(tableModel: RestaurantTableModel) {
        val resource = mTablesData.value
        if (resource?.data == null) return
        if (resource.data.contains(tableModel)) return
        mTablesData.value = Resource.cloneResource(resource, resource.data.toMutableList().apply { add(0, tableModel) })
    }

    override fun updateResults() {
        if (mTablesData.value?.status != Resource.Status.LOADING) fetchActiveTables(shopPk)
    }

    fun updateRemoveTable(sessionPk: Long) = mTablesData.value?.data?.run {
        val pos = indexOfFirstOrNull { it.sessionPk == sessionPk } ?: return@run
        get(pos).removeFromDb()
        val result = toMutableList().apply {
            removeAt(pos)
        }
        mTablesData.value = Resource.cloneResource(mTablesData.value, result)
    }

    fun updateTable(sessionPk: Long, event: EventBriefModel) {
        val result = mTablesData.value?.data?.toMutableList()?.apply {
            val pos = indexOfFirstOrNull { it.sessionPk == sessionPk } ?: return@apply
            val table = get(pos)
            table.addEvent(event)
            if (event.type == SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT) table.tableSession?.isRequestedCheckout = true
            removeAt(pos)
            add(0, table)
        } ?: return
        mTablesData.setValue(Resource.cloneResource(mTablesData.value, result))
    }

    fun processQrPk(qrPk: Long) {
        val requestJson = Converters.objectMapper.createObjectNode()
        requestJson.put("qr", qrPk)
        mQrResult.addSource(mManagerRepository.managerInitiateSession(requestJson), mQrResult::setValue)
    }
}
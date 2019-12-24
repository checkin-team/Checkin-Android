package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Data.BaseViewModel
import com.checkin.app.checkin.Data.Converters
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Waiter.WaiterRepository
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.CheckoutStatusModel
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.QRResultModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import java.util.ArrayList
import kotlin.Comparator
import kotlin.Int
import kotlin.Long
import kotlin.apply

class ManagerASLiveTablesViewModel(application: Application) : BaseViewModel(application) {
    private val mManagerRepository: ManagerRepository = ManagerRepository.getInstance(application)
    private val mWaiterRepository: WaiterRepository = WaiterRepository.getInstance(application)

    private val mTablesData = createNetworkLiveData<List<RestaurantTableModel>>()
    private val mCheckoutData = createNetworkLiveData<CheckoutStatusModel>()
    private val mQrResult = createNetworkLiveData<QRResultModel>()

    var shopPk: Long = 0

    val sessionInitiated: LiveData<Resource<QRResultModel>> = mQrResult

    val checkoutData: LiveData<Resource<CheckoutStatusModel>> = mCheckoutData


    val activeTables: LiveData<Resource<List<RestaurantTableModel>>>
        get() = Transformations.map(mTablesData) { input ->
            if (input?.data == null || input.status !== Resource.Status.SUCCESS) return@map input
            val result: MutableList<RestaurantTableModel> = ArrayList()
            for (i in input.data.indices) {
                val tableModel = input.data[i]
                if (tableModel.tableSession != null) result.add(tableModel)
            }
            result.sortWith(Comparator { t1: RestaurantTableModel, t2: RestaurantTableModel ->
                if (t2.tableSession!!.event != null && t1.tableSession!!.event != null) {
                    t2.tableSession!!.event.timestamp.compareTo(t1.tableSession!!.event.timestamp)
                } else {
                    t2.tableSession!!.created.compareTo(t1.tableSession!!.created)
                }
            })
            Resource.cloneResource(input, result)
        }

    val inactiveTables: LiveData<Resource<List<RestaurantTableModel>>>
        get() = Transformations.map(mTablesData) { input ->
            if (input?.data == null || input.status !== Resource.Status.SUCCESS) return@map input
            val result: MutableList<RestaurantTableModel> = ArrayList()
            for (i in input.data.indices) {
                val tableModel = input.data[i]
                if (tableModel.tableSession == null) result.add(tableModel)
            }
            Resource.cloneResource(input, result)
        }

    fun markSessionDone(sessionId: Long) {
        val data = Converters.objectMapper.createObjectNode()
        data.put("payment_mode", "csh")
        mCheckoutData.addSource(mManagerRepository.manageSessionCheckout(sessionId), mCheckoutData::setValue)
    }

    fun fetchActiveTables(restaurantId: Long) {
        shopPk = restaurantId
        mTablesData.addSource(mWaiterRepository.getShopTables(restaurantId, false), mTablesData::setValue)
    }

    fun getTablePositionWithPk(sessionPk: Long): Int {
        val resource = mTablesData.value
        if (resource?.data == null) return -1
        for (i in resource.data.indices) {
            val tableSessionModel = resource.data[i].tableSession
            if (tableSessionModel != null && tableSessionModel.pk == sessionPk) {
                return i
            }
        }
        return -1
    }

    fun getTableWithPosition(position: Int): RestaurantTableModel? {
        val resource = mTablesData.value
        if (resource?.data == null) return null
        return if (position >= resource.data.size) null else resource.data[position]
    }

    fun addRestaurantTable(tableModel: RestaurantTableModel) {
        val resource = mTablesData.value
        if (resource?.data == null) return
        if (resource.data.contains(tableModel)) return
        mTablesData.setValue(Resource.cloneResource(resource, resource.data.toMutableList().apply { add(0, tableModel) }))
    }

    override fun updateResults() {
        fetchActiveTables(shopPk)
    }

    fun updateRemoveTable(sessionPk: Long) {
        val resource = mTablesData.value
        if (resource?.data == null) return
        var pos = -1
        for (i in resource.data.indices) {
            val tableSessionModel = resource.data[i].tableSession
            if (tableSessionModel != null && tableSessionModel.pk == sessionPk) {
                pos = i
                break
            }
        }
        if (pos > -1)
            mTablesData.value = Resource.cloneResource(resource, resource.data.toMutableList().apply { removeAt(pos) })
    }

    fun updateTable(sessionPk: Long, event: EventBriefModel) {
        val listResource = mTablesData.value
        if (listResource?.data == null) return
        var pos = 0
        for (i in listResource.data.indices) {
            val tableSessionModel = listResource.data[i].tableSession
            if (tableSessionModel != null && tableSessionModel.pk == sessionPk) {
                pos = i
                val table = listResource.data[pos]
                if (table != null) {
                    tableSessionModel.event = event
                    if (event.type == SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT) tableSessionModel.isRequestedCheckout = true
                    table.addEventCount()
                    mTablesData.setValue(Resource.cloneResource(listResource, listResource.data.toMutableList().apply {
                        removeAt(pos)
                        add(0, table)
                    }))
                }
            }
        }
    }

    fun processQrPk(qrPk: Long) {
        val requestJson = Converters.objectMapper.createObjectNode()
        requestJson.put("qr", qrPk)
        mQrResult.addSource(mManagerRepository.managerInitiateSession(requestJson)) { value: Resource<QRResultModel> -> mQrResult.setValue(value) }
    }
}
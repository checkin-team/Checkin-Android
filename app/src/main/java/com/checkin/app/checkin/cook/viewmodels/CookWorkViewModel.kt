package com.checkin.app.checkin.cook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.checkin.app.checkin.Waiter.WaiterRepository
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.data.resource.Resource.Companion.cloneResource
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.utility.indexOfFirstOrNull

class CookWorkViewModel(application: Application) : BaseViewModel(application) {
    private val mWaiterRepository: WaiterRepository = WaiterRepository.getInstance(application)

    private val mTablesData = createNetworkLiveData<List<RestaurantTableModel>>()

    var shopPk: Long = 0
        private set

    fun fetchActiveTables(restaurantId: Long) {
        shopPk = restaurantId
        mTablesData.addSource(mWaiterRepository.filterShopTables(restaurantId, true), mTablesData::setValue)
    }

    val activeTables: LiveData<Resource<List<RestaurantTableModel>>> = Transformations.map(mTablesData) { input ->
        if (input?.data == null) return@map input
        val results = input.data.run {
            sortedWith(RestaurantTableModel.sortComparator)
        }
        cloneResource(input, results)
    }

    fun getTablePositionWithPk(sessionPk: Long): Int = mTablesData.value?.data?.indexOfFirst { it.sessionPk == sessionPk }
            ?: -1

    fun getTableWithPosition(position: Int): RestaurantTableModel? = mTablesData.value?.data?.getOrNull(position)

    fun addRestaurantTable(tableModel: RestaurantTableModel) {
        val result = mTablesData.value?.data?.toMutableList()?.apply {
            add(0, tableModel)
        } ?: return
        mTablesData.value = cloneResource(mTablesData.value, result)
    }

    override fun updateResults() {
        fetchActiveTables(shopPk)
    }

    fun updateRemoveTable(sessionPk: Long) {
        val pos = getTablePositionWithPk(sessionPk).takeIf { it != -1 } ?: return
        getTableWithPosition(pos)?.removeFromDb()
        val result = mTablesData.value?.data?.toMutableList()?.apply {
            removeAt(pos)
        }
        mTablesData.value = cloneResource(mTablesData.value, result)
    }

    fun updateTable(sessionPk: Long, newOrderPk: Long) {
        val result = mTablesData.value?.data?.toMutableList()?.apply {
            val pos = indexOfFirstOrNull { it.sessionPk == sessionPk } ?: return@apply
            val table = get(pos)
            table.addEvent(EventBriefModel(newOrderPk, SessionChatModel.CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM, "New order"))

            removeAt(pos)
            add(0, table)
        } ?: return
        mTablesData.value = cloneResource(mTablesData.value, result)
    }
}
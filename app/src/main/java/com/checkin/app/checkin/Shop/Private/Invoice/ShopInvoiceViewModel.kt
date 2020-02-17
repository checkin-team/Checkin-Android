package com.checkin.app.checkin.Shop.Private.Invoice

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource

class ShopInvoiceViewModel(application: Application) : BaseViewModel(application) {
    private val mShopInvoiceRepository: ShopInvoiceRepository = ShopInvoiceRepository.getInstance(application)

    public var mShopPk: Long = 0
    private var fromDate: String? = null
    private var toDate: String? = null
    private var mPrevResults: LiveData<Resource<List<RestaurantSessionModel>>>? = null
    private val mResults = createNetworkLiveData<List<RestaurantSessionModel>>()

    private var mPrevStatsResults: LiveData<Resource<RestaurantAdminStatsModel>>? = null
    private val mStatsResults = createNetworkLiveData<RestaurantAdminStatsModel>()

    val restaurantSessions: LiveData<Resource<List<RestaurantSessionModel>>>
        get() = mResults

    val restaurantStats: LiveData<Resource<RestaurantAdminStatsModel>>
        get() = mStatsResults

    var doFetchStats: Boolean = false

    fun fetchShopSessions(restaurantId: Long) {
        this.mShopPk = restaurantId
        mPrevResults = mShopInvoiceRepository.getRestaurantSessions(restaurantId)
        mResults.addSource(mPrevResults!!) { mResults.value = it }
    }

    fun fetchShopStats(restaurantId: Long) {
        this.mShopPk = restaurantId
        mPrevStatsResults = mShopInvoiceRepository.getRestaurantAdminStats(restaurantId)
        mStatsResults.addSource(mPrevStatsResults!!) { mStatsResults.value = it }
    }

    fun setShopPk(restaurantId: Long) {
        this.mShopPk = restaurantId
    }

    private fun filterRestaurantSessions(fromDate: String?, toDate: String?) {
        if (mPrevResults != null)
            mResults.removeSource(mPrevResults!!)
        mPrevResults = mShopInvoiceRepository.getRestaurantSessions(mShopPk, fromDate, toDate)
        mResults.addSource(mPrevResults!!) { mResults.value = it }
    }

    private fun filterRestaurantStats(fromDate: String?, toDate: String?) {
        if (mPrevStatsResults != null)
            mStatsResults.removeSource(mPrevStatsResults!!)
        mPrevStatsResults = mShopInvoiceRepository.getRestaurantAdminStats(mShopPk, fromDate, toDate)
        mStatsResults.addSource(mPrevStatsResults!!) { mStatsResults.value = it }
    }

    fun filterFrom(fromDate: String) {
        this.fromDate = fromDate
        filterRestaurantSessions(fromDate, toDate)
        if (doFetchStats) filterRestaurantStats(fromDate, toDate)
    }

    fun filterTo(toDate: String) {
        this.toDate = toDate
        filterRestaurantSessions(fromDate, toDate)
        if (doFetchStats) filterRestaurantStats(fromDate, toDate)
    }

    override fun updateResults() {
        fetchShopSessions(mShopPk)
        if (doFetchStats) fetchShopStats(mShopPk)
    }
}

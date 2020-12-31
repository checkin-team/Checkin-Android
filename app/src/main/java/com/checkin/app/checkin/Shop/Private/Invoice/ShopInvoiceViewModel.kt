package com.checkin.app.checkin.Shop.Private.Invoice

import android.app.Application
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource

class ShopInvoiceViewModel(application: Application) : BaseViewModel(application) {
    private val mShopInvoiceRepository: ShopInvoiceRepository = ShopInvoiceRepository.getInstance(application)

    private var mShopPk: Long = 0
    private var fromDate: String? = null
    private var toDate: String? = null
    private var mPrevResults: LiveData<Resource<List<RestaurantSessionModel>>>? = null
    private val mResults = createNetworkLiveData<List<RestaurantSessionModel>>()

    private var mPrevStatsResults: LiveData<Resource<RestaurantAdminStatsModel>>? = null
    private val mStatsResults = createNetworkLiveData<RestaurantAdminStatsModel>()

    val restaurantSessions: LiveData<Resource<List<RestaurantSessionModel>>> = mResults
    val restaurantStats: LiveData<Resource<RestaurantAdminStatsModel>> = mStatsResults

    var doFetchStats: Boolean = false

    fun fetchShopSessions(restaurantId: Long = mShopPk) {
        this.mShopPk = restaurantId
        if (mPrevResults != null)
            mResults.removeSource(mPrevResults!!)
        mPrevResults = if (fromDate == null && toDate == null) mShopInvoiceRepository.getRestaurantSessions(restaurantId)
        else mShopInvoiceRepository.getRestaurantSessions(restaurantId, fromDate, toDate)
        mResults.addSource(mPrevResults!!, mResults::setValue)
    }

    fun fetchShopStats(restaurantId: Long) {
        this.mShopPk = restaurantId
        mPrevStatsResults = mShopInvoiceRepository.getRestaurantAdminStats(restaurantId)
        mStatsResults.addSource(mPrevStatsResults!!, mStatsResults::setValue)
    }

    private fun filterRestaurantStats(fromDate: String?, toDate: String?) {
        if (mPrevStatsResults != null)
            mStatsResults.removeSource(mPrevStatsResults!!)
        mPrevStatsResults = mShopInvoiceRepository.getRestaurantAdminStats(mShopPk, fromDate, toDate)
        mStatsResults.addSource(mPrevStatsResults!!, mStatsResults::setValue)
    }

    @JvmOverloads
    fun filterFrom(fromDate: String, doFetch: Boolean = true) {
        this.fromDate = fromDate
        if (doFetch) {
            fetchShopSessions()
            if (doFetchStats) filterRestaurantStats(fromDate, toDate)
        }
    }

    @JvmOverloads
    fun filterTo(toDate: String, doFetch: Boolean = true) {
        this.toDate = toDate
        if (doFetch) {
            fetchShopSessions()
            if (doFetchStats) filterRestaurantStats(fromDate, toDate)
        }
    }

    override fun updateResults() {
        fetchShopSessions()
        if (doFetchStats) fetchShopStats(mShopPk)
    }
}

package com.checkin.app.checkin.Shop.Private.Invoice

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Data.*

class ShopInvoiceRepository(context: Context) {
    private val mWebService: WebApiService = ApiClient.getApiService(context)

    @JvmOverloads
    fun getRestaurantSessions(restaurantId: Long, fromDate: String? = null, toDate: String? = null): LiveData<Resource<List<RestaurantSessionModel>>> {
        return object : NetworkBoundResource<List<RestaurantSessionModel>, List<RestaurantSessionModel>>() {

            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<List<RestaurantSessionModel>>> {
                return RetrofitLiveData(mWebService.getRestaurantSessionsById(restaurantId, fromDate, toDate))
            }

            override fun saveCallResult(data: List<RestaurantSessionModel>) {

            }
        }.asLiveData
    }

    fun getShopSessionDetail(sessionId: Long): LiveData<Resource<ShopSessionDetailModel>> {
        return object : NetworkBoundResource<ShopSessionDetailModel, ShopSessionDetailModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<ShopSessionDetailModel>> {
                return RetrofitLiveData(mWebService.getShopSessionDetailById(sessionId))
            }

            override fun saveCallResult(data: ShopSessionDetailModel) {

            }
        }.asLiveData
    }

    fun getRestaurantSessionReviews(sessionId: Long): LiveData<Resource<List<ShopSessionFeedbackModel>>> {
        return object : NetworkBoundResource<List<ShopSessionFeedbackModel>, List<ShopSessionFeedbackModel>>() {
            override fun shouldUseLocalDb(): Boolean {
                return false
            }

            override fun createCall(): LiveData<ApiResponse<List<ShopSessionFeedbackModel>>> {
                return RetrofitLiveData(mWebService.getRestaurantSessionReviews(sessionId))
            }

            override fun saveCallResult(data: List<ShopSessionFeedbackModel>) {

            }
        }.asLiveData
    }

    @JvmOverloads
    fun getRestaurantAdminStats(restaurantId: Long, fromDate: String? = null, toDate: String? = null): LiveData<Resource<RestaurantAdminStatsModel>> {
        return object : NetworkBoundResource<RestaurantAdminStatsModel, RestaurantAdminStatsModel>() {
            override fun shouldUseLocalDb(): Boolean = false

            override fun createCall(): LiveData<ApiResponse<RestaurantAdminStatsModel>> = RetrofitLiveData(mWebService.getRestaurantAdminStats(restaurantId, fromDate, toDate))

            override fun saveCallResult(data: RestaurantAdminStatsModel?) {
            }
        }.asLiveData
    }

    companion object {
        private var INSTANCE: ShopInvoiceRepository? = null

        fun getInstance(application: Application): ShopInvoiceRepository = INSTANCE ?: {
            synchronized(ShopInvoiceRepository::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = ShopInvoiceRepository(application.applicationContext)
                }
            }
            INSTANCE!!
        }()
    }
}

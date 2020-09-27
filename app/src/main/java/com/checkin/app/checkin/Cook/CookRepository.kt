package com.checkin.app.checkin.Cook

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.Cook.Model.CookTableModel
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.ObjectBoxInstanceLiveData
import com.checkin.app.checkin.data.db.dbStore
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.MenuRepository
import com.checkin.app.checkin.menu.models.MenuModel
import com.checkin.app.checkin.menu.models.MenuModel_
import com.checkin.app.checkin.menu.models.NewOrderModel
import com.checkin.app.checkin.utility.SingletonHolder

/**
 * Created by Shivansh Saini on 24/01/2019.
 */
class CookRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)
    private val cookox by dbStore<CookTableModel>()
    fun getActiveTables(shopId: Long): LiveData<Resource<List<CookTableModel>>> {
        return object : NetworkBoundResource<List<CookTableModel>, List<CookTableModel>>()  {
            override fun shouldUseLocalDb(): Boolean =true





            override fun createCall(): LiveData<ApiResponse<List<CookTableModel>>> {
                return RetrofitLiveData(mWebService.getCookActiveTables(shopId))
            }


        }.asLiveData
    }

    companion object : SingletonHolder<CookRepository, Application>({ CookRepository(it.applicationContext) })

}
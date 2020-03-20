package com.checkin.app.checkin.accounts

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.checkin.app.checkin.data.BaseRepository
import com.checkin.app.checkin.data.db.AppDatabase
import com.checkin.app.checkin.data.network.ApiClient.Companion.getApiService
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.network.RetrofitLiveData
import com.checkin.app.checkin.data.network.WebApiService
import com.checkin.app.checkin.data.resource.NetworkBoundResource
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.utility.SingletonHolder
import io.objectbox.android.ObjectBoxLiveData

class AccountRepository private constructor(context: Context) : BaseRepository() {
    private val mWebService: WebApiService = getApiService(context)
    private val boxAccount = AppDatabase.boxFor<AccountModel>()

    val selfAccounts: LiveData<Resource<List<AccountModel>>>
        get() = object : NetworkBoundResource<List<AccountModel>, List<AccountModel>>() {
            override fun shouldUseLocalDb(): Boolean = true

            override fun createCall(): LiveData<ApiResponse<List<AccountModel>>> {
                return RetrofitLiveData(mWebService.selfAccounts)
            }

            override fun loadFromDb(): LiveData<List<AccountModel>>? = ObjectBoxLiveData(boxAccount.query().build())

            override fun saveCallResult(data: List<AccountModel>?) {
                if (data != null) {
                    boxAccount.removeAll()
                    boxAccount.put(data)
                }
            }
        }.asLiveData

    companion object : SingletonHolder<AccountRepository, Application>({ AccountRepository(it.applicationContext) })
}
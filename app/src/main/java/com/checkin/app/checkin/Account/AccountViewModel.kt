package com.checkin.app.checkin.Account

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource

class AccountViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: AccountRepository = AccountRepository.getInstance(application)

    private val mAccounts = createNetworkLiveData<List<AccountModel>>()
    private val mCurrentAccount = MutableLiveData<AccountModel>()
    val accounts: LiveData<Resource<List<AccountModel>>>
        get() {
            fetchAccounts()
            return mAccounts
        }

    fun fetchAccounts() {
        if (mAccounts.value?.status != Resource.Status.SUCCESS)
            mAccounts.addSource(mRepository.selfAccounts, mAccounts::setValue)
    }

    val currentAccount: LiveData<AccountModel> = mCurrentAccount

    fun setCurrentAccount(account: AccountModel) {
        mCurrentAccount.value = account
    }

    override fun updateResults() {
        fetchAccounts()
    }
}
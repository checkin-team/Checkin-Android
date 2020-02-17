package com.checkin.app.checkin.accounts

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource

class AccountViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: AccountRepository = AccountRepository.getInstance(application)

    private val mAccounts = createNetworkLiveData<List<AccountModel>>()
    private val mCurrentAccount = MutableLiveData<Long>()

    val accounts: LiveData<Resource<List<AccountModel>>>
        get() {
            fetchAccounts()
            return mAccounts
        }
    val currentAccount: LiveData<Long> = mCurrentAccount

    fun fetchAccounts() {
        if (mAccounts.value?.isSuccess != true)
            mAccounts.addSource(mRepository.selfAccounts, mAccounts::setValue)
    }

    fun setCurrentAccount(identifier: Long) {
        mCurrentAccount.value = identifier
    }

    override fun updateResults() {
        fetchAccounts()
    }
}
package com.checkin.app.checkin.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.checkin.app.checkin.utility.Constants

object AuthPreferences {
    fun getAuthToken(context: Context?): String? {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE)
        return if (accounts.isNotEmpty()) {
            accountManager.peekAuthToken(accounts[0], AccountManager.KEY_AUTHTOKEN)
        } else null
    }

    fun getCurrentAccount(context: Context?): Account? {
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE)
        return accounts.getOrNull(0)
    }

    @JvmStatic
    fun removeCurrentAccount(context: Context?): Boolean {
        val account = getCurrentAccount(context)
        return if (account != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            AccountManager.get(context).removeAccountExplicitly(account)
        } else false
    }
}
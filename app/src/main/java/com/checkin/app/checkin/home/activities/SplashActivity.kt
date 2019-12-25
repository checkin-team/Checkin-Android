package com.checkin.app.checkin.home.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE
import com.checkin.app.checkin.Auth.AuthActivity
import com.checkin.app.checkin.Auth.AuthPreferences
import com.checkin.app.checkin.Auth.DeviceTokenService
import com.checkin.app.checkin.Cook.CookWorkActivity
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity
import com.checkin.app.checkin.Utility.Constants
import com.checkin.app.checkin.Waiter.WaiterWorkActivity
import com.checkin.app.checkin.home.activities.HomeActivity
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goForScreens()
    }

    private fun goForScreens() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val account = AuthPreferences.getCurrentAccount(this)
        if (account == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        if (!prefs.getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            startService(Intent(applicationContext, DeviceTokenService::class.java))
        }
        val accountTag = prefs.getInt(Constants.SP_LAST_ACCOUNT_TYPE, ACCOUNT_TYPE.USER.id)
        val accountPk = prefs.getLong(Constants.SP_LAST_ACCOUNT_PK, 0)
        val intent = when (ACCOUNT_TYPE.getById(accountTag)) {
            ACCOUNT_TYPE.SHOP_OWNER, ACCOUNT_TYPE.SHOP_ADMIN ->
                Intent(this, ShopPrivateActivity::class.java).apply { putExtra(ShopPrivateActivity.KEY_SHOP_PK, accountPk) }
            ACCOUNT_TYPE.RESTAURANT_WAITER ->
                Intent(this, WaiterWorkActivity::class.java).apply { putExtra(WaiterWorkActivity.KEY_SHOP_PK, accountPk) }
            ACCOUNT_TYPE.RESTAURANT_COOK ->
                Intent(this, CookWorkActivity::class.java).apply { putExtra(CookWorkActivity.KEY_RESTAURANT_PK, accountPk) }
            ACCOUNT_TYPE.RESTAURANT_MANAGER ->
                Intent(this, ManagerWorkActivity::class.java).apply { putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, accountPk) }
            else -> Intent(this, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
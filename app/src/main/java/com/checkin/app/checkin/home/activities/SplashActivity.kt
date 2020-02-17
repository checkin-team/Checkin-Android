package com.checkin.app.checkin.home.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.checkin.app.checkin.Cook.CookWorkActivity
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity
import com.checkin.app.checkin.Waiter.WaiterWorkActivity
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.auth.AuthPreferences
import com.checkin.app.checkin.auth.activities.AuthActivity
import com.checkin.app.checkin.auth.services.DeviceTokenService
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.coroutineLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
//    @BindView(R.id.lottie_splash)
//    internal lateinit var lottieSplash: LottieAnimationView

    private var doubleBackPressedToExit = false
    private var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)
        // Activates the fetched config from Firebase Remote Config
        RemoteConfig.activate()

        goForScreens()
//        lottieSplash.addAnimatorListener(this)
    }

    /*override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {
        if (!loaded) {
            loaded = true
            coroutineLifecycleScope.launch { goForScreens() }
        }
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
*/
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
        val accountTag = prefs.getInt(Constants.SP_LAST_ACCOUNT_TYPE, ACCOUNT_TYPE.USER.value)
        val accountPk = prefs.getLong(Constants.SP_LAST_ACCOUNT_PK, 0)
        val intent = when (ACCOUNT_TYPE.getByValue(accountTag)) {
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

    override fun onBackPressed() {
        if (doubleBackPressedToExit) super.onBackPressed()
        else {
            doubleBackPressedToExit = true
            Utils.toast(this, "Press back again to exit")
            coroutineLifecycleScope.launch {
                delay(2000)
                doubleBackPressedToExit = false
            }
        }
    }
}
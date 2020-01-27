package com.checkin.app.checkin.accounts

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.imageloader.drawerImageLoader
import com.checkin.app.checkin.Cook.CookWorkActivity
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity
import com.checkin.app.checkin.Utility.Constants
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.Waiter.WaiterWorkActivity
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.activities.AboutUsActivity
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.util.DrawerUIUtils

@SuppressLint("Registered")
abstract class BaseAccountActivity : BaseActivity() {
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    open val toolbarView: Toolbar? = null

    @LayoutRes
    open val footerRes: Int = R.layout.incl_leftnav_footer

    abstract val accountTypes: Array<ACCOUNT_TYPE>

    protected val accountViewModel: AccountViewModel by viewModels()

    protected lateinit var mainDrawer: Drawer

    @CallSuper
    protected open fun setupUi() {
        onInitDrawer()
        setupAccountHeader()
    }

    private fun setupAccountHeader() {
        val type = prefs.getInt(Constants.SP_LAST_ACCOUNT_TYPE, 201)
        val pk = prefs.getLong(Constants.SP_LAST_ACCOUNT_PK, 0)
        accountViewModel.accounts.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> it.data?.let {
                        if (AccountUtil.getUsername(this) == null) {
                            val name = it.find { it.accountType == ACCOUNT_TYPE.USER }?.name?.split("\\s")?.get(0)
                                    ?: ""
                            PreferenceManager.getDefaultSharedPreferences(this).edit()
                                    .putString(Constants.SP_USER_PROFILE_NAME, name)
                                    .apply()
                        }
                        var currIndex = it.indexOfFirst { it.accountType.id == type && (pk == 0L || pk == it.targetPk) }
                        if (currIndex == -1) currIndex = 0
                        accountViewModel.setCurrentAccount(currIndex.toLong())
                    }
                    Resource.Status.LOADING -> pass
                    else -> accountViewModel.fetchAccounts()
                }
            }
        })
        accountViewModel.currentAccount.observe(this, Observer {
            onUpdateDrawer()
        })
    }

    protected open fun setupFooter() {
        val btnAboutUs: Button = mainDrawer.stickyFooter!!.findViewById(R.id.btn_about_us)
        val btnLogout: Button = mainDrawer.stickyFooter!!.findViewById(R.id.btn_logout)
        btnAboutUs.setOnClickListener { startActivity(Intent(this, AboutUsActivity::class.java)) }
        btnLogout.setOnClickListener { onLogoutClick() }
    }

    @CallSuper
    protected open fun buildDrawer() {
        mainDrawer = drawer {
            toolbarView?.let { toolbar = it }
            accountHeader {
                val accounts = accountViewModel.accounts.value?.data
                background = R.drawable.background_drawer_header
                accounts?.forEachIndexed { index, accountModel ->
                    profile(accountModel.name, accountModel.formatAccountType) {
                        identifier = index.toLong()
                        nameShown = true
                        if (accountModel.pic != null) iconUrl = accountModel.pic
                        else icon = if (accountModel.accountType == ACCOUNT_TYPE.USER) R.drawable.cover_unknown_male else R.drawable.cover_restaurant_unknown
                    }
                }
                onProfileChanged { _, profile, current ->
                    val acc = accounts?.get(profile.identifier.toInt())
                            ?: return@onProfileChanged false
                    if (current) false
                    else {
                        switchAccount(acc)
                        true
                    }
                }
            }.setActiveProfile(accountViewModel.currentAccount.value?.toLong() ?: 0)
            actionBarDrawerToggleAnimated = true

            setupDrawerItems(this)

            drawerImageLoader {
                placeholder { ctx, tag ->
                    when (tag) {
                        else -> DrawerUIUtils.getPlaceHolder(ctx)
                    }
                }
                cancel {
                    GlideApp.with(it).clear(it)
                }
                set { imageView, uri, placeholder, tag ->
                    GlideApp.with(imageView)
                            .load(uri)
                            .placeholder(placeholder)
                            .into(imageView)
                }
            }
            stickyFooterShadow = false
            stickyFooterRes = footerRes
        }
        setupFooter()
    }

    @CallSuper
    protected open fun onInitDrawer() {
        buildDrawer()
    }

    @CallSuper
    protected open fun onUpdateDrawer() {
        buildDrawer()
    }

    protected abstract fun setupDrawerItems(builder: DrawerBuilderKt)

    private fun switchAccount(account: AccountModel) {
        prefs.edit()
                .putInt(Constants.SP_LAST_ACCOUNT_TYPE, account.accountType.id)
                .putLong(Constants.SP_LAST_ACCOUNT_PK, account.targetPk)
                .apply()

        val intent = when (account.accountType) {
            ACCOUNT_TYPE.USER -> {
                Utils.navigateBackToHome(this)
                return
            }
            ACCOUNT_TYPE.SHOP_OWNER, ACCOUNT_TYPE.SHOP_ADMIN -> Intent.makeRestartActivityTask(ComponentName(this, ShopPrivateActivity::class.java)).apply {
                putExtra(ShopPrivateActivity.KEY_SHOP_PK, account.targetPk)
            }
            ACCOUNT_TYPE.RESTAURANT_MANAGER -> Intent.makeRestartActivityTask(ComponentName(this, ManagerWorkActivity::class.java)).apply {
                putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, account.targetPk)
            }
            ACCOUNT_TYPE.RESTAURANT_WAITER -> Intent.makeRestartActivityTask(ComponentName(this, WaiterWorkActivity::class.java)).apply {
                putExtra(WaiterWorkActivity.KEY_SHOP_PK, account.targetPk)
            }
            ACCOUNT_TYPE.RESTAURANT_COOK -> Intent.makeRestartActivityTask(ComponentName(this, CookWorkActivity::class.java)).apply {
                putExtra(CookWorkActivity.KEY_RESTAURANT_PK, account.targetPk)
            }
        }
        startActivity(intent)
    }

    protected open fun onLogoutClick() {
        if (!Utils.logoutFromApp(applicationContext))
            Utils.toast(applicationContext, "Unable to logout. Manually remove account from settings.")
    }

    companion object {
        private val TAG = BaseAccountActivity::class.simpleName
    }
}
package com.checkin.app.checkin.home.activities

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.checkin.app.checkin.BuildConfig
import com.checkin.app.checkin.R
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.accounts.BaseAccountActivity
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.notifications.ActiveSessionNotificationService
import com.checkin.app.checkin.data.notifications.Constants
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.ProblemModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.fragments.UserHomeFragment
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.LiveSessionViewModel
import com.checkin.app.checkin.location.UserCurrentLocationService
import com.checkin.app.checkin.misc.activities.QRScannerActivity
import com.checkin.app.checkin.misc.adapters.BaseFragmentAdapterBottomNav
import com.checkin.app.checkin.misc.fragments.BlankFragment
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.misc.fragments.QRScannerWrapperFragment
import com.checkin.app.checkin.misc.views.DynamicSwipableViewPager
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.checkin.app.checkin.user.fragments.UserPrivateProfileFragment
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.user.viewmodels.UserViewModel
import com.checkin.app.checkin.utility.*
import com.checkin.app.checkin.utility.OnBoardingUtils.OnBoardingModel
import com.golovin.fluentstackbar.FluentSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class HomeActivity : BaseAccountActivity() {
    @BindView(R.id.toolbar_home)
    internal lateinit var toolbarHome: Toolbar
    @BindView(R.id.tabs_home)
    internal lateinit var tabLayout: TabLayout
    @BindView(R.id.vp_home)
    internal lateinit var vpHome: DynamicSwipableViewPager
    @BindView(R.id.container_home_cart)
    internal lateinit var containerCart: ViewGroup
    @BindView(R.id.tv_home_cart_restaurant)
    internal lateinit var tvCartRestaurant: TextView
    @BindView(R.id.im_home_cart_restaurant)
    internal lateinit var imCartRestaurant: ImageView

    var imTabUserIcon: ImageView? = null
    private val mViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val liveViewModel: LiveSessionViewModel by viewModels()
    private val networkFragment = NetworkBlockingFragment()

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = MessageUtils.parseMessage(intent) ?: return
            when (message.type) {
                MESSAGE_TYPE.USER_SESSION_ADDED_BY_OWNER -> {
                    mViewModel.updateResults()
                    openActiveSession()
                }
                MESSAGE_TYPE.SHOP_MEMBER_ADDED -> accountViewModel.updateResults()
            }
        }
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            val coords = intent?.getSerializableExtra(UserCurrentLocationService.KEY_ACTION_LOCATION_COORDINATES) as Pair<Double, Double>
            mViewModel.fetchNearbyRestaurants()
        }
    }

    override val toolbarView: Toolbar?
        get() = toolbarHome

    override val accountTypes: Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.USER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        setupUi()

        val adapter = HomeFragmentAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(vpHome)
        vpHome.adapter = adapter
        vpHome.isEnabled = false
        adapter.setupWithTab(tabLayout, vpHome)
        vpHome.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    2 -> {
                        toolbarHome.visibility = View.GONE
                        imTabUserIcon!!.background = resources.getDrawable(R.drawable.shape_oval_orange_gradient)
                        imTabUserIcon!!.setPadding(6, 6, 6, 6)
                        onCancelCartView()
                    }
                    1 -> {
                        launchScanner()
                        vpHome.currentItem = 0
                        resetUserIcon()
                    }
                    else -> {
                        toolbarHome.visibility = View.VISIBLE
                        resetUserIcon()
                    }
                }
            }
        })

        setupUserLocationTracker()
        setupObserver()
        explainQr()

        // Refresh Remote Config
        RemoteConfig.refresh().addOnSuccessListener {
            RemoteConfig.activate()
        }
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }
    }

    private fun requestEnableLocation() {
        if (isLocationEnabled)
            trackUserCurrentLocation()
        else {
            runOnUiThread {
                FluentSnackbar.create(this)
                        .create(R.string.request_turn_on_gps)
                        .duration(Snackbar.LENGTH_INDEFINITE)
                        .neutralBackgroundColor()
                        .actionTextRes(R.string.settings)
                        .action {
                            // Build intent that displays the App settings screen.
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivityForResult(intent, REQUEST_SETTINGS_LOCATION)
                        }
                        .show()
            }
        }

    }

    override fun updateScreen() {
        accountViewModel.updateResults()
    }

    private fun setupObserver() {
        accountViewModel.accounts.observe(this, Observer {
            if (it?.isSuccess == true && it.data != null) {
                val acc = it.data.find { it.accountType == ACCOUNT_TYPE.USER } ?: return@Observer
                if (imTabUserIcon != null) {
                    Utils.loadImageOrDefault(imTabUserIcon, acc.pic, R.drawable.cover_unknown_male)
                }
            }
        })
        userViewModel.userData.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    val data = resource.data
                    if (imTabUserIcon != null) {
                        Utils.loadImageOrDefault(
                                imTabUserIcon, data.profilePic,
                                if (data.gender == UserModel.GENDER.MALE) R.drawable.cover_unknown_male else R.drawable.cover_unknown_female
                        )
                    }
                    stopRefreshing()
                } else if (resource.status === Resource.Status.LOADING) startRefreshing() else stopRefreshing()
            }
        })
        mViewModel.qrResult.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    mViewModel.updateResults()
                    liveViewModel.updateResults()
                    if (!resource.data.isMasterQr) openActiveSession()
                    else openPublicRestaurantProfile(resource.data.restaurantPk, resource.data.sessionPk)
                } else if (resource.status !== Resource.Status.LOADING) {
                    Utils.toast(this, resource.message)
                }
            }
        })
        mViewModel.sessionStatus.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    val serviceIntent = Intent(this, ActiveSessionNotificationService::class.java)
                    serviceIntent.action = Constants.SERVICE_ACTION_FOREGROUND_START
                    serviceIntent.putExtra(ActiveSessionNotificationService.ACTIVE_RESTAURANT_DETAIL, resource.data.restaurant)
                    serviceIntent.putExtra(ActiveSessionNotificationService.ACTIVE_SESSION_PK, resource.data.pk)
                    startService(serviceIntent)
                } else if (resource.status === Resource.Status.ERROR_NOT_FOUND) {
                    ActiveSessionNotificationService.clearNotification(applicationContext)
                } else if (resource.problem != null && resource.problem!!.getErrorCode() === ProblemModel.ERROR_CODE.SESSION_USER_PENDING_MEMBER) {
                    // User is pending member of a active session
                } else pass
            }
        })
        mViewModel.cancelDineInData.observe(this, Observer {
            it?.let { objectNodeResource ->
                if (objectNodeResource.status === Resource.Status.SUCCESS) {
                }
            }
        })
        liveViewModel.cartStatus.observe(this, Observer {
            it?.data?.let {
                containerCart.visibility = View.VISIBLE
                Utils.loadImageOrDefault(imCartRestaurant, it.restaurant.target.displayPic, R.drawable.cover_restaurant_unknown)
                tvCartRestaurant.text = it.restaurant.target.displayName
            } ?: run {
                containerCart.visibility = View.GONE
            }
        })
        liveViewModel.clearCartData.observe(this, Observer { })
        mViewModel.fetchSessionStatus()
        mViewModel.fetchNearbyRestaurants()
        liveViewModel.fetchCartStatus()
    }

    private fun resetUserIcon() {
        imTabUserIcon!!.background = null
        imTabUserIcon!!.setPadding(0, 0, 0, 0)
    }

    private fun explainQr() {
        val tab = tabLayout.getTabAt(1)
        if (tab != null) {
            val qrView = tab.customView
            OnBoardingUtils.conditionalOnBoarding(this, SP_ONBOARDING_QR_SCANNER, true, OnBoardingModel("Scan Checkin QR!", qrView, false))
        }
    }

    override fun setupDrawerItems(builder: DrawerBuilderKt) = builder.run {
        selectedItem = -1
        primaryItem {
            name = "My Transactions"
            enabled = true
            icon = R.drawable.ic_order_summary
            textColorRes = R.color.brownish_grey
            onClick { _ ->
                val intent = PaymentTransactionActivity.withIntent(this@HomeActivity)
                startActivity(intent)
                false
            }
        }

        pass
    }

    private fun setupUserLocationTracker() {
        if (hasLocationPermission) requestEnableLocation()
        else requestLocationPermission()
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        if (shouldProvideRationale) {
            FluentSnackbar.create(this)
                    .create(R.string.user_location_permission_rationale)
                    .duration(Snackbar.LENGTH_INDEFINITE)
                    .neutralBackgroundColor()
                    .maxLines(2)
                    .actionTextRes(R.string.allow)
                    .action {
                        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_LOCATION_PERMISSION)
                    }
                    .show()
        } else ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_LOCATION_PERMISSION)
    }

    private fun trackUserCurrentLocation() {
        val serviceIntent = Intent(this, UserCurrentLocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(serviceIntent)
        else startService(serviceIntent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_QR_SCANNER && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(QRScannerWrapperFragment.KEY_QR_RESULT)
            mViewModel.processQr(qrData)
        } else if (requestCode == REQUEST_SETTINGS_LOCATION) {
            if (isLocationEnabled) trackUserCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                when {
                    grantResults.isEmpty() -> setupUserLocationTracker()
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> trackUserCurrentLocation()
                    else -> {
                        FluentSnackbar.create(this)
                                .create(R.string.location_permission_denied_explanation)
                                .duration(Snackbar.LENGTH_INDEFINITE)
                                .errorBackgroundColor()
                                .maxLines(2)
                                .actionTextRes(R.string.settings)
                                .action {
                                    // Build intent that displays the App settings screen.
                                    val intent = Intent().apply {
                                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intent)
                                }
                                .show()
                    }
                }
            }
        }
    }

    private fun launchScanner() {
        val intent = Intent(this, QRScannerActivity::class.java)
        startActivityForResult(intent, REQUEST_QR_SCANNER)
    }

    override fun onResume() {
        super.onResume()
        accountViewModel.fetchAccounts()
        mViewModel.updateResults()
        liveViewModel.updateResults()
        val types = arrayOf(MESSAGE_TYPE.USER_SESSION_ADDED_BY_OWNER, MESSAGE_TYPE.SHOP_MEMBER_ADDED)
        MessageUtils.registerLocalReceiver(this, mReceiver, *types)
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, IntentFilter().apply {
            addAction(UserCurrentLocationService.ACTION_BROADCAST_LOCATION)
        })
    }

    private fun openActiveSession() {
        startActivity(Intent(this, ActiveSessionActivity::class.java))
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(this, mReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)
    }

    @OnClick(R.id.im_home_cart_cancel)
    fun onCancelCartView() {
        liveViewModel.clearCart()
    }

    @OnClick(R.id.container_home_cart)
    fun onClickCart() {
        liveViewModel.cartStatus.value?.data?.let { openPublicRestaurantProfile(it.restaurant.targetId, it.pk, true) }
    }

    private inner class HomeFragmentAdapter(fm: FragmentManager) : BaseFragmentAdapterBottomNav(fm) {
        override fun getTabDrawable(position: Int): Int = when (position) {
            0 -> R.drawable.ic_home_toggle
            1 -> R.drawable.ic_qr_code_grey
            else -> 0
        }

        override fun getCustomView(position: Int): Int {
            return if (position == 2) R.layout.view_tab_bottom_nav_circle else super.getCustomView(position)
        }

        override fun bindTabIcon(imIcon: ImageView, position: Int) {
            if (position == 2) {
                imTabUserIcon = imIcon
                imIcon.setImageResource(R.drawable.cover_unknown_male)
            } else super.bindTabIcon(imIcon, position)
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> UserHomeFragment.newInstance()
            2 -> UserPrivateProfileFragment.newInstance()
            else -> BlankFragment.newInstance()
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "Home"
            1 -> "Scan"
            else -> null
        }
    }

    companion object {
        const val SP_ONBOARDING_QR_SCANNER = "onboarding.qrscanner"
        private const val REQUEST_QR_SCANNER = 212
        private const val REQUEST_LOCATION_PERMISSION = 123
        private const val REQUEST_SETTINGS_LOCATION = 111
    }
}
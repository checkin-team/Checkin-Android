package com.checkin.app.checkin.home.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE
import com.checkin.app.checkin.Account.BaseAccountActivity
import com.checkin.app.checkin.Data.Message.ActiveSessionNotificationService
import com.checkin.app.checkin.Data.Message.Constants
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE
import com.checkin.app.checkin.Data.Message.MessageUtils
import com.checkin.app.checkin.Data.ProblemModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.ShopJoin.BusinessFeaturesActivity
import com.checkin.app.checkin.User.Private.UserPrivateProfileFragment
import com.checkin.app.checkin.User.Private.UserViewModel
import com.checkin.app.checkin.User.UserModel
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager
import com.checkin.app.checkin.Utility.OnBoardingUtils
import com.checkin.app.checkin.Utility.OnBoardingUtils.OnBoardingModel
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.home.fragments.UserHomeFragment
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.LiveSessionViewModel
import com.checkin.app.checkin.misc.activities.QRScannerActivity
import com.checkin.app.checkin.misc.adapters.BaseFragmentAdapterBottomNav
import com.checkin.app.checkin.misc.fragments.BlankFragment
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class HomeActivity : BaseAccountActivity(), NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar_home)
    internal lateinit var toolbarHome: Toolbar
    @BindView(R.id.drawer_home)
    internal lateinit var drawerLayout: DrawerLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)

        configureToolbar()

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

        setupObserver()
        explainQr()
    }

    private fun configureToolbar() {
        navAccount.setNavigationItemSelectedListener(this)
        val startToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(startToggle)
        startToggle.syncState()
        setSupportActionBar(toolbarHome)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_navigation_grey)
    }

    override fun updateScreen() {
        accountViewModel.updateResults()
    }

    private fun setupObserver() {
        userViewModel.userData.observe(this, Observer { resource ->
            if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                val data = resource.data
                if (imTabUserIcon != null) {
                    Utils.loadImageOrDefault(imTabUserIcon, data.profilePic, if (data.gender == UserModel.GENDER.MALE) R.drawable.cover_unknown_male else R.drawable.cover_unknown_female)
                }
                stopRefreshing()
            } else if (resource.status === Resource.Status.LOADING) startRefreshing() else stopRefreshing()
        })
        mViewModel.qrResult.observe(this, Observer { resource ->
            if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                mViewModel.updateResults()
                liveViewModel.updateResults()
                if (!resource.data.isMasterQr) openActiveSession()
                else openPublicRestaurantProfile(resource.data.restaurantPk, resource.data.sessionPk)
            } else if (resource.status !== Resource.Status.LOADING) {
                Utils.toast(this, resource.message)
            }
        })
        mViewModel.sessionStatus.observe(this, Observer { resource ->
            if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                val serviceIntent = Intent(this, ActiveSessionNotificationService::class.java)
                serviceIntent.action = Constants.SERVICE_ACTION_FOREGROUND_START
                serviceIntent.putExtra(ActiveSessionNotificationService.ACTIVE_RESTAURANT_DETAIL, resource.data.restaurant)
                serviceIntent.putExtra(ActiveSessionNotificationService.ACTIVE_SESSION_PK, resource.data.pk)
                startService(serviceIntent)
            } else if (resource.status === Resource.Status.ERROR_NOT_FOUND) {
                ActiveSessionNotificationService.clearNotification(applicationContext)
            } else if (resource.problem != null && resource.problem!!.getErrorCode() === ProblemModel.ERROR_CODE.SESSION_USER_PENDING_MEMBER) {
                //                tvSessionWaitQRBusy.setText(resource.getProblem().getDetail());
            }
        })
        mViewModel.cancelDineInData.observe(this, Observer { objectNodeResource ->
            if (objectNodeResource.status === Resource.Status.SUCCESS) {
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

    override fun getDrawerRootId(): Int = R.id.drawer_home

    override fun getNavMenu(): Int = R.menu.drawer_home

    override fun <T : AccountHeaderViewHolder?> getHeaderViewHolder(): T = AccountHeaderViewHolder(this, R.layout.layout_header_account) as T

    override fun getAccountTypes(): Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.USER)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_QR_SCANNER && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(QRScannerActivity.KEY_QR_RESULT)
            mViewModel.processQr(qrData)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.nav_new_shop -> {
            startActivity(Intent(this, BusinessFeaturesActivity::class.java))
            true
        }
        else -> false
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
    }

    private fun openActiveSession() {
        startActivity(Intent(this, ActiveSessionActivity::class.java))
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(this, mReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(GravityCompat.START)
        return true
    }

    @OnClick(R.id.im_home_cart_cancel)
    fun onCancelCartView() {
        liveViewModel.clearCart()
    }

    @OnClick(R.id.container_home_cart)
    fun onClickCart() {
        liveViewModel.cartStatus.value?.data?.let { openPublicRestaurantProfile(it.restaurant.targetId, it.pk) }
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
    }
}
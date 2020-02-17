package com.checkin.app.checkin.manager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel
import com.checkin.app.checkin.Shop.ShopPreferences
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.accounts.BaseAccountActivity
import com.checkin.app.checkin.manager.fragments.*
import com.checkin.app.checkin.manager.fragments.ManagerTablesActivateFragment.LiveOrdersInteraction
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.adapters.BaseFragmentAdapterBottomNav
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.misc.views.DynamicSwipableViewPager
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.inTransaction
import com.google.android.material.tabs.TabLayout


class ManagerWorkActivity : BaseAccountActivity(), LiveOrdersInteraction {
    @BindView(R.id.pager_manager_work)
    internal lateinit var pagerManager: DynamicSwipableViewPager
    @BindView(R.id.tabs_manager_work)
    internal lateinit var tabLayout: TabLayout
    @BindView(R.id.toolbar_manager_work)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.tv_action_bar_title)
    internal lateinit var tvActionBarTitle: TextView
    @BindView(R.id.sw_live_order)
    internal lateinit var swLiveOrdersToggle: SwitchCompat

    private val mViewModel: ManagerWorkViewModel by viewModels()
    private val mShopViewModel: ShopInvoiceViewModel by viewModels()
    private val networkFragment = NetworkBlockingFragment()

    override val accountTypes: Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.RESTAURANT_MANAGER)

    override val toolbarView: Toolbar?
        get() = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_work)
        ButterKnife.bind(this)

        setupUi()

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            elevation = resources.getDimension(R.dimen.card_elevation)
            setDisplayHomeAsUpEnabled(true)
        }
        setupObservers(intent.getLongExtra(KEY_RESTAURANT_PK, 0L))
        pagerManager.isEnabled = false
        val pagerAdapter = ManagerFragmentAdapter(supportFragmentManager)
        pagerManager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(pagerManager)
        pagerAdapter.setupWithTab(tabLayout, pagerManager)
        pagerManager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (actionBar != null) {
                    tvActionBarTitle.text = pagerAdapter.getPageTitle(position)
                }
            }
        })
        swLiveOrdersToggle.setOnCheckedChangeListener { _, isChecked ->
            pagerAdapter.setActivated(isChecked)
            pagerAdapter.setupWithTab(tabLayout, pagerManager)
            ShopPreferences.setManagerLiveOrdersActivated(this, isChecked)
        }

        // Set Initial state of orders activation
        val isActivated = ShopPreferences.isManagerLiveOrdersActivated(this)
        setLiveOrdersActivation(isActivated)
        pagerAdapter.oldValActivated = isActivated


        val shouldOpenInvoice = intent.getBooleanExtra(KEY_NOTIF_OPEN_LAST_TABLES, false)
        if (shouldOpenInvoice) {
            pagerManager.setCurrentItem(2, true)
        } else {
            val sessionBundle = intent.getBundleExtra(KEY_SESSION_NOTIFICATION_BUNDLE)
            if (sessionBundle != null) {
                val sessionPk = sessionBundle.getLong(KEY_NOTIF_SESSION_PK)
                val intent = when (sessionBundle.getSerializable(KEY_NOTIF_LIVE_ORDER_TYPE) as? RestaurantOrdersFragmentType) {
                    RestaurantOrdersFragmentType.ACTIVE_SESSION -> Intent(this, ManagerSessionActivity::class.java).apply {
                        putExtra(ManagerSessionActivity.KEY_SESSION_PK, sessionPk)
                        putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.shopPk)
                        putExtra(ManagerSessionActivity.KEY_OPEN_ORDERS, sessionBundle.getBoolean(ManagerSessionActivity.KEY_OPEN_ORDERS))
                    }
                    RestaurantOrdersFragmentType.MASTER_QR -> ManagerQSRDetailActivity.withSessionIntent(this, sessionPk)
                    RestaurantOrdersFragmentType.PRE_ORDER -> ManagerPreOrderDetailActivity.withSessionIntent(this, sessionPk)
                    else -> null
                }
                intent?.let { startActivityForResult(it, RC_INTENT_RESULT_SESSION_DETAIL) }
            }
        }
        supportFragmentManager.inTransaction {
            add(android.R.id.content, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }
    }

    override fun setupDrawerItems(builder: DrawerBuilderKt) {
    }

    private fun setupObservers(shopId: Long) {
        mViewModel.fetchRestaurantData(shopId)
        mShopViewModel.fetchShopSessions(shopId)
        mShopViewModel.filterFrom(Utils.getCurrentFormattedDateInvoice())
        mShopViewModel.filterTo(Utils.getCurrentFormattedDateInvoice())
    }

    override fun setLiveOrdersActivation(isActivated: Boolean) {
        swLiveOrdersToggle.isChecked = isActivated
    }

    override fun updateScreen() {
        super.updateScreen()
        accountViewModel.updateResults()
        mViewModel.updateResults()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_INTENT_RESULT_SESSION_DETAIL -> updateScreen()
        }
    }

    internal class ManagerFragmentAdapter(fm: FragmentManager) : BaseFragmentAdapterBottomNav(fm) {
        var oldValActivated = true
        private var isActivated = true
        private val mTableFragment = ManagerLiveOrdersFragment.newInstance()
        private val mActiveTableFragment = ManagerTablesActivateFragment.newInstance()

        override fun getTabDrawable(position: Int): Int = when (position) {
            0 -> R.drawable.ic_orders_list_toggle
            1 -> R.drawable.ic_stats_toggle
            2 -> R.drawable.ic_invoice_toggle
            else -> 0
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> if (isActivated) mTableFragment else mActiveTableFragment
            1 -> ManagerStatsFragment.newInstance()
            else -> ManagerInvoiceFragment.newInstance()
        }

        override fun getItemPosition(obj: Any): Int = if (oldValActivated != isActivated) {
            oldValActivated = isActivated
            PagerAdapter.POSITION_NONE
        } else PagerAdapter.POSITION_UNCHANGED

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "Live Orders"
            1 -> "Insight"
            2 -> "Invoice"
            else -> null
        }

        fun setActivated(isChecked: Boolean) {
            isActivated = isChecked
            notifyDataSetChanged()
        }
    }

    companion object {
        private const val RC_INTENT_RESULT_SESSION_DETAIL = 12

        const val KEY_RESTAURANT_PK = "manager.restaurant_pk"
        const val KEY_NOTIF_OPEN_LAST_TABLES = "manager.notif.show_last_tables"
        const val KEY_NOTIF_LIVE_ORDER_TYPE = "manager.notif.live_order.type"
        const val KEY_NOTIF_SESSION_PK = "manager.notif.session_pk"
        const val KEY_SESSION_NOTIFICATION_BUNDLE = "manager.session_notification_bundle"
    }
}
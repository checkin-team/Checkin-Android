package com.checkin.app.checkin.manager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE
import com.checkin.app.checkin.Account.BaseAccountActivity
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceViewModel
import com.checkin.app.checkin.Shop.ShopPreferences
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.manager.fragments.ManagerInvoiceFragment
import com.checkin.app.checkin.manager.fragments.ManagerLiveOrdersFragment
import com.checkin.app.checkin.manager.fragments.ManagerStatsFragment
import com.checkin.app.checkin.manager.fragments.ManagerTablesActivateFragment
import com.checkin.app.checkin.manager.fragments.ManagerTablesActivateFragment.LiveOrdersInteraction
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.adapters.BaseFragmentAdapterBottomNav
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.google.android.material.tabs.TabLayout

class ManagerWorkActivity : BaseAccountActivity(), LiveOrdersInteraction {
    @BindView(R.id.pager_manager_work)
    internal lateinit var pagerManager: DynamicSwipableViewPager
    @BindView(R.id.tabs_manager_work)
    internal lateinit var tabLayout: TabLayout
    @BindView(R.id.drawer_manager_work)
    internal lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.toolbar_manager_work)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.tv_action_bar_title)
    internal lateinit var tvActionBarTitle: TextView
    @BindView(R.id.sw_live_order)
    internal lateinit var swLiveOrdersToggle: SwitchCompat

    private val mViewModel: ManagerWorkViewModel by viewModels()
    private val mShopViewModel: ShopInvoiceViewModel by viewModels()
    private val networkViewModel: BlockingNetworkViewModel by viewModels()
    private val networkFragment = NetworkBlockingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_work)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.elevation = resources.getDimension(R.dimen.card_elevation)
            actionBar.setDisplayHomeAsUpEnabled(true)
            val startToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawerLayout.addDrawerListener(startToggle)
            startToggle.syncState()
        }
        initRefreshScreen(R.id.sr_manager_work)
        setupObservers(intent.getLongExtra(KEY_RESTAURANT_PK, 0L))
        pagerManager.isEnabled = false
        val pagerAdapter = ManagerFragmentAdapter(supportFragmentManager)
        pagerManager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(pagerManager)
        pagerAdapter.setupWithTab(tabLayout, pagerManager)
        pagerManager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (actionBar != null) {
                    if (position == 0) {
                        tvActionBarTitle.text = "Live Orders"
                    } else if (position == 1) {
                        tvActionBarTitle.text = "Insight"
                    } else if (position == 2) {
                        tvActionBarTitle.text = "Invoice"
                    }
                }
            }
        })
        swLiveOrdersToggle.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            pagerAdapter.setActivated(isChecked)
            pagerAdapter.setupWithTab(tabLayout, pagerManager)
            ShopPreferences.setManagerLiveOrdersActivated(this, isChecked)
        }
        setLiveOrdersActivation(ShopPreferences.isManagerLiveOrdersActivated(this))
        val shouldOpenInvoice = intent.getBooleanExtra(KEY_OPEN_LAST_TABLES, false)
        if (shouldOpenInvoice) {
            pagerManager.setCurrentItem(2, true)
        } else {
            val sessionBundle = intent.getBundleExtra(KEY_SESSION_BUNDLE)
            if (sessionBundle != null) {
                val intent = Intent(this, ManagerSessionActivity::class.java)
                intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, sessionBundle.getLong(ManagerSessionActivity.KEY_SESSION_PK))
                        .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.shopPk)
                        .putExtra(ManagerSessionActivity.KEY_OPEN_ORDERS, sessionBundle.getBoolean(ManagerSessionActivity.KEY_OPEN_ORDERS))
                startActivity(intent)
            }
        }
        supportFragmentManager.inTransaction {
            add(android.R.id.content, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }
    }

    private fun setupObservers(shopId: Long) {
        mViewModel.fetchRestaurantData(shopId)
        mShopViewModel.fetchShopSessions(shopId)
        mShopViewModel.filterFrom(Utils.getCurrentFormattedDateInvoice())
        mShopViewModel.filterTo(Utils.getCurrentFormattedDateInvoice())
        networkViewModel.shouldTryAgain.observe(this, Observer {
            mViewModel.fetchRestaurantData(shopId)
        })
    }

    override fun getDrawerRootId(): Int {
        return R.id.drawer_manager_work
    }

    override fun getNavMenu(): Int {
        return R.menu.drawer_manager_work
    }

    override fun <T : AccountHeaderViewHolder?> getHeaderViewHolder(): T {
        return AccountHeaderViewHolder(this, R.layout.layout_header_account) as T
    }

    override fun getAccountTypes(): Array<ACCOUNT_TYPE> {
        return arrayOf(ACCOUNT_TYPE.RESTAURANT_MANAGER)
    }

    override fun setLiveOrdersActivation(isActivated: Boolean) {
        swLiveOrdersToggle.isChecked = isActivated
    }

    override fun updateScreen() {
        super.updateScreen()
        accountViewModel.updateResults()
        mViewModel.updateResults()
        mViewModel.fetchStatistics()
    }

    internal class ManagerFragmentAdapter(fm: FragmentManager?) : BaseFragmentAdapterBottomNav(fm) {
        private var isActivated = true
        private val mTableFragment = ManagerLiveOrdersFragment.newInstance()
        private val mActiveTableFragment = ManagerTablesActivateFragment.newInstance()
        override fun getTabDrawable(position: Int): Int {
            return when (position) {
                0 -> R.drawable.ic_orders_list_toggle
                1 -> R.drawable.ic_stats_toggle
                2 -> R.drawable.ic_invoice_toggle
                else -> 0
            }
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> if (isActivated) mTableFragment else mActiveTableFragment
            1 -> ManagerStatsFragment.newInstance()
            else -> ManagerInvoiceFragment.newInstance()
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Live Orders"
                1 -> return "Insight"
                2 -> return "Invoice"
            }
            return null
        }

        fun setActivated(isChecked: Boolean) {
            isActivated = isChecked
            notifyDataSetChanged()
        }
    }

    companion object {
        const val KEY_OPEN_LAST_TABLES = "manager.show_last_tables"
        const val KEY_RESTAURANT_PK = "manager.restaurant_pk"
        const val KEY_SESSION_BUNDLE = "manager.session_bundle"
    }
}
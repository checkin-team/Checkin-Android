package com.checkin.app.checkin.Shop.Private

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import com.checkin.app.checkin.Inventory.InventoryActivity
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.Insight.ShopInsightActivity
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.accounts.BaseAccountActivity
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.adapters.BaseFragmentAdapterBottomNav
import com.checkin.app.checkin.misc.fragments.BlankFragment
import com.checkin.app.checkin.misc.views.DynamicSwipableViewPager
import com.checkin.app.checkin.utility.Utils
import com.google.android.material.tabs.TabLayout

class ShopPrivateActivity : BaseAccountActivity() {
    @BindView(R.id.sr_shop_private)
    internal lateinit var swipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.vp_shop_private)
    internal lateinit var vpShopPrivate: DynamicSwipableViewPager
    @BindView(R.id.tabs_shop_private)
    internal lateinit var tabsShopPrivate: TabLayout
    @BindView(R.id.iv_shop_profile_navigation)
    internal lateinit var ivShopProfileNavigation: ImageView

    private val mViewModel: ShopProfileViewModel by viewModels()

    override val accountTypes: Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.SHOP_ADMIN, ACCOUNT_TYPE.SHOP_OWNER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_private)
        ButterKnife.bind(this)

        setupUi()
        initRefreshScreen(R.id.sr_shop_private)

        val shopPk = intent.getLongExtra(KEY_SHOP_PK, 0)
        mViewModel.fetchShopDetails(shopPk)
        mViewModel.shopData.observe(this, Observer { restaurantModelResource ->
            if (restaurantModelResource.status === Resource.Status.SUCCESS && restaurantModelResource.data != null) {
                stopRefreshing()
            } else if (restaurantModelResource.status === Resource.Status.LOADING) {
                startRefreshing()
            } else {
                stopRefreshing()
                Utils.toast(this, restaurantModelResource.message)
            }
        })
        setup()
    }

    private fun setup() {
        val adapter = ShopFragmentAdapter(supportFragmentManager)
        vpShopPrivate.isEnabled = false
        vpShopPrivate.adapter = adapter
        tabsShopPrivate.setupWithViewPager(vpShopPrivate)
        adapter.setupWithTab(tabsShopPrivate, vpShopPrivate)
        vpShopPrivate.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 1) launchShopInsight() else if (position == 2) launchMenu()
                vpShopPrivate.currentItem = 0
                super.onPageSelected(position)
            }
        })
    }

    fun enableDisableSwipeRefresh(enable: Boolean) {
        swipeRefreshLayout.isEnabled = enable
    }

    override fun setupDrawerItems(builder: DrawerBuilderKt) {

    }

    @OnClick(R.id.btn_shop_private_insight)
    fun onInsight() {
        launchShopInsight()
    }

    @OnClick(R.id.iv_shop_profile_navigation)
    fun onViewClicked() {
        mainDrawer.openDrawer()
    }

    private fun launchMenu() {
        val intent = Intent(this, InventoryActivity::class.java)
        intent.putExtra(InventoryActivity.KEY_INVENTORY_RESTAURANT_PK, mViewModel.shopPk)
        startActivity(intent)
    }

    private fun launchShopInsight() {
        val intent = Intent(this, ShopInsightActivity::class.java)
        intent.putExtra(InventoryActivity.KEY_INVENTORY_RESTAURANT_PK, mViewModel.shopPk)
        startActivity(intent)
    }

    override fun updateScreen() {
        accountViewModel.updateResults()
        mViewModel.updateResults()
    }

    private inner class ShopFragmentAdapter internal constructor(fm: FragmentManager?) : BaseFragmentAdapterBottomNav(fm!!) {
        override fun getTabDrawable(position: Int): Int = when (position) {
            0 -> R.drawable.ic_tab_home_toggle
            2 -> R.drawable.ic_tab_menu_toggle
            else -> 0
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> ShopPrivateProfileFragment.newInstance()
            else -> BlankFragment.newInstance()
        }

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "Shop"
            2 -> "Inventory"
            else -> null
        }

        override fun getCount(): Int = 3

        override fun onTabClick(position: Int) {
            if (position == 2) launchMenu() else super.onTabClick(position)
        }
    }

    companion object {
        const val KEY_SHOP_PK = "shop_private.pk"
    }
}
package com.checkin.app.checkin.restaurant.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Menu.MenuItemInteraction
import com.checkin.app.checkin.Menu.Model.MenuItemModel
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel
import com.checkin.app.checkin.Shop.RestaurantModel
import com.checkin.app.checkin.Utility.inTransaction
import com.checkin.app.checkin.Utility.setTabBackground
import com.checkin.app.checkin.menu.fragments.UserMenuFragment
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.adapters.CoverPagerAdapter
import com.checkin.app.checkin.misc.fragments.BlankFragment
import com.checkin.app.checkin.misc.fragments.FragmentInteraction
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import kotlin.math.abs

class PublicRestaurantProfileActivity : BaseActivity(), MenuItemInteraction, AppBarLayout.OnOffsetChangedListener, FragmentInteraction {
    @BindView(R.id.indicator_restaurant_public_covers)
    internal lateinit var indicatorTopCover: PageIndicatorView
    @BindView(R.id.fragment_vp_restaurant_public)
    internal lateinit var vpFragment: ViewPager2
    @BindView(R.id.tabs_restaurant_public)
    internal lateinit var tabsFragment: TabLayout
    @BindView(R.id.tv_restaurant_public_name_locality)
    internal lateinit var tvName: TextView
    @BindView(R.id.vp_restaurant_public_covers)
    internal lateinit var vpRestaurantCovers: ViewPager
    @BindView(R.id.tv_restaurant_public_cuisines)
    internal lateinit var tvRestaurantCuisines: TextView
    @BindView(R.id.tv_restaurant_public_count_checkins)
    internal lateinit var tvCountCheckins: TextView
    @BindView(R.id.tv_restaurant_public_rating)
    internal lateinit var tvRating: TextView
    @BindView(R.id.tv_restaurant_public_distance)
    internal lateinit var tvDistance: TextView
    @BindView(R.id.appbar_restaurant_public)
    internal lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar_restaurant_public)
    internal lateinit var toolbar: Toolbar

    val fragmentAdapter = PublicRestaurantProfileAdapter(this)
    val coverAdapter = CoverPagerAdapter()

    val restaurantViewModel: ShopProfileViewModel by viewModels()
    var restaurantId: Long = 0

    private var fragmentList = mutableListOf<Fragment>()

    private var isTabAtTop = false
    private var title = ""

    val adapter = PublicRestaurantProfileAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_restaurant_profile)
        ButterKnife.bind(this)

        initUi()
        setupObservers()
    }

    private fun initUi() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        appbar.addOnOffsetChangedListener(this)

        vpRestaurantCovers.adapter = coverAdapter
        indicatorTopCover.setViewPager(vpRestaurantCovers)
        indicatorTopCover.setAnimationType(AnimationType.FILL)
        indicatorTopCover.setClickListener { position -> vpRestaurantCovers.currentItem = position }

        vpFragment.adapter = fragmentAdapter
        TabLayoutMediator(tabsFragment, vpFragment) { _, _ -> }
        tabsFragment.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(null) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vpFragment.currentItem = tab.position
            }
        })
        restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0)
        restaurantViewModel.fetchShopDetails(restaurantId)
    }

    private fun setupObservers() {
        restaurantViewModel.shopData.observe(this, Observer {
            it?.also { restaurantResource ->
                when (restaurantResource.status) {
                    Resource.Status.SUCCESS -> restaurantResource.data?.let { setupData(it) }
                }
            }
        })
    }

    private fun setupData(restaurantModel: RestaurantModel) {
        title = restaurantModel.name
        if (isTabAtTop) toolbar.title = title
        tvName.text = title
        tvCountCheckins.text = "Checkins ${restaurantModel.formatCheckins()}"
        coverAdapter.setData(*restaurantModel.covers)
        tvRestaurantCuisines.text = restaurantModel.cuisines?.let {
            if (it.isNotEmpty()) {
                val maxLen = it.size.coerceAtMost(3)
                it.slice(0 until maxLen).joinToString(" ")
            } else "-"
        } ?: "-"
        tvRating.text = "1.2"
        tvDistance.text = "3kms"
    }

    override fun onMenuItemAdded(item: MenuItemModel): Boolean {
        TODO()
    }

    override fun onMenuItemChanged(item: MenuItemModel, count: Int): Boolean {
        TODO()
    }

    override fun onMenuItemShowInfo(item: MenuItemModel) {
    }

    override fun getItemOrderedCount(item: MenuItemModel): Int {
        TODO()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentOffset = abs(verticalOffset).toFloat() / maxScroll
        if (percentOffset >= .7f && !isTabAtTop) {
            tabsFragment.run {
                setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.white))
                setTabTextColors(ContextCompat.getColor(context, R.color.pinkish_grey), ContextCompat.getColor(context, R.color.white))
                setTabBackground(ContextCompat.getColor(context, R.color.teal_blue))
            }
            isTabAtTop = true
            toolbar.title = title
        } else if (percentOffset < .7f && isTabAtTop) {
            title = toolbar.title.toString()
            toolbar.title = ""
            tabsFragment.run {
                setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.teal_blue))
                setTabTextColors(ContextCompat.getColor(context, R.color.brownish_grey), ContextCompat.getColor(context, R.color.teal_blue))
                setTabBackground(ContextCompat.getColor(context, R.color.white))
            }
            isTabAtTop = false
        }
    }

    override fun onAddFragment(fragment: Fragment, tag: String?) {
        fragmentList.add(fragment)
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, fragment, tag)
        }
    }

    companion object {
        const val KEY_RESTAURANT_ID = "restaurant_profile.public.id"
    }

    inner class PublicRestaurantProfileAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        val tabs = listOf(RestaurantTab.MENU, RestaurantTab.INFO, RestaurantTab.REVIEWS)

        override fun getItemCount() = tabs.size

        override fun createFragment(position: Int): Fragment = when (tabs[position]) {
            RestaurantTab.MENU -> UserMenuFragment.newInstance(restaurantId)
            else -> BlankFragment.newInstance()
        }

        fun getTitle(position: Int): CharSequence? = tabs[position].name
    }

    enum class RestaurantTab {
        MENU, INFO, REVIEWS
    }
}

fun Context.openPublicRestaurantProfile(restaurantId: Long) {
    startActivity(Intent(this, PublicRestaurantProfileActivity::class.java).apply {
        putExtra(PublicRestaurantProfileActivity.KEY_RESTAURANT_ID, restaurantId)
    })
}

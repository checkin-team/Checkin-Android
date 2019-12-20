package com.checkin.app.checkin.restaurant.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.Data.ProblemModel
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.*
import com.checkin.app.checkin.menu.fragments.UserMenuFragment
import com.checkin.app.checkin.menu.viewmodels.CartViewModel
import com.checkin.app.checkin.menu.viewmodels.SessionType
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.activities.QRScannerActivity
import com.checkin.app.checkin.misc.adapters.CoverPagerAdapter
import com.checkin.app.checkin.misc.fragments.*
import com.checkin.app.checkin.restaurant.models.RestaurantBriefModel
import com.checkin.app.checkin.restaurant.models.RestaurantModel
import com.checkin.app.checkin.restaurant.viewmodels.RestaurantPublicViewModel
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.checkin.app.checkin.session.scheduled.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import java.util.*
import kotlin.math.abs

class PublicRestaurantProfileActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener,
        ScheduledSessionInteraction, QRScannerWrapperInteraction, NewSessionCreationInteraction, SchedulerInteraction {
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
    @BindView(R.id.nested_sv_restaurant_public)
    internal lateinit var nestedSv: NestedScrollView
    @BindView(R.id.scheduled_cart_restaurant_public)
    internal lateinit var scheduledCartView: ScheduledSessionCartView

    private lateinit var fragmentAdapter: PublicRestaurantProfileAdapter
    private val coverAdapter = CoverPagerAdapter()

    private val cartViewModel: CartViewModel by viewModels()
    private val restaurantViewModel: RestaurantPublicViewModel by viewModels()
    private val scheduledSessionViewModel: ScheduledSessionViewModel by viewModels()

    var restaurantId: Long = 0
    private var isTabAtTop = false
    private var mRestaurantData: RestaurantModel? = null
    private var title = ""
    private var allowOrder = true

    private var networkFragment: NetworkBlockingFragment = NetworkBlockingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_restaurant_profile)
        ButterKnife.bind(this)

        initUi()
        setupObservers()
    }

    private fun initUi() {
        restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0)

        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        appbar.addOnOffsetChangedListener(this)

        vpRestaurantCovers.adapter = coverAdapter
        indicatorTopCover.setViewPager(vpRestaurantCovers)
        indicatorTopCover.setAnimationType(AnimationType.FILL)
        indicatorTopCover.setClickListener { position -> vpRestaurantCovers.currentItem = position }

        fragmentAdapter = PublicRestaurantProfileAdapter(this, restaurantId)
        vpFragment.adapter = fragmentAdapter
        TabLayoutMediator(tabsFragment, vpFragment) { _, _ -> }
        tabsFragment.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(null) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vpFragment.currentItem = tab.position
            }
        })
        restaurantViewModel.fetchRestaurantWithId(restaurantId)
        cartViewModel.sessionType = SessionType.SCHEDULED

        scheduledCartView.setup(this)

        val params = appbar.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null)
            params.behavior = AppBarLayout.Behavior()
        val behaviour = params.behavior as AppBarLayout.Behavior
        behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                // Disable flicking the appbar when cart view is expanded
                return !scheduledCartView.isExpanded()
            }
        })

        supportFragmentManager.inTransaction {
            add(android.R.id.content, networkFragment, NetworkBlockingFragment.FRAGMENT_TAG)
        }
    }

    private fun setupObservers() {
        cartViewModel.fetchCartStatus()

        cartViewModel.cartStatus.observe(this, Observer {
            it?.let {
                if (it.status == Resource.Status.SUCCESS && it.data != null) {
                    allowOrder = it.data.restaurant.targetId == restaurantId
                    if (allowOrder) {
                        successNewSession(it.data.pk)
                        cartViewModel.fetchCartOrders()
                    }
                }
            }
        })

        restaurantViewModel.restaurantData.observe(this, Observer {
            it?.also { restaurantResource ->
                when (restaurantResource.status) {
                    Resource.Status.SUCCESS -> restaurantResource.data?.let { setupData(it) }
                    else -> pass
                }
            }
        })

        scheduledSessionViewModel.newScheduledSessionData.observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> successNewSession(resource.data!!.pk)
                    Resource.Status.ERROR_INVALID_REQUEST -> if (resource.problem?.getErrorCode() == ProblemModel.ERROR_CODE.SESSION_SCHEDULED_PENDING_CART) {
                        val cartRestaurant = cartViewModel.cartStatus.value?.data?.restaurant?.target
                        if (cartRestaurant != null) handleErrorCartExists(cartRestaurant)
                    }
                }
            }
        })

        scheduledSessionViewModel.newQrSessionData.observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> resource.data?.run {
                        if (isMasterQr && restaurantPk == restaurantId) successNewSession(sessionPk)
                        else if (isMasterQr) wrongRestaurantQrScanned()
                        else startActivity(Intent(this@PublicRestaurantProfileActivity, ActiveSessionActivity::class.java))
                    }
                    else -> pass
                }
            }
        })

        scheduledSessionViewModel.clearCartData.observe(this, Observer {
            if (it?.status == Resource.Status.SUCCESS) clearSession()
        })
    }

    private fun handleErrorCartExists(cartRestaurant: RestaurantBriefModel) {
        AlertDialog.Builder(this)
                .setTitle("Cart item exists")
                .setMessage("Are you sure you want to clear the cart of ${cartRestaurant.displayName}?")
                .setPositiveButton("Yes") { _, _ -> scheduledSessionViewModel.clearCart() }
                .setNegativeButton("No, take me to restaurant") { dialog, _ ->
                    dialog.cancel()
                    openPublicRestaurantProfile(cartRestaurant.pk)
                    finish()
                }
    }

    private fun wrongRestaurantQrScanned() {
        Utils.toast(this, "Wrong Restaurant!")
    }

    @OnClick(R.id.tv_restaurant_public_navigate)
    fun onClickNavigate() {
        mRestaurantData?.location?.run { navigateToLocation(this@PublicRestaurantProfileActivity) }
    }

    private fun successNewSession(sessionPk: Long) {
        cartViewModel.sessionPk = sessionPk
        scheduledSessionViewModel.sessionPk = sessionPk
        cartViewModel.syncOrdersWithServer()
    }

    private fun clearSession() {
        cartViewModel.sessionPk = 0L
        scheduledSessionViewModel.sessionPk = 0L
    }

    private fun setupData(restaurantModel: RestaurantModel) {
        mRestaurantData = restaurantModel

        title = restaurantModel.name
        if (isTabAtTop) toolbar.title = title
        tvName.text = title
        tvCountCheckins.text = "Checkins ${restaurantModel.formatCheckins()}"
        coverAdapter.setData(restaurantModel.covers)
        tvRestaurantCuisines.text = restaurantModel.cuisines?.let {
            if (it.isNotEmpty()) {
                val maxLen = it.size.coerceAtMost(3)
                it.slice(0 until maxLen).joinToString(" ")
            } else "-"
        } ?: "-"
        tvRating.text = restaurantModel.rating.toString()
        tvDistance.text = ""
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

    override fun onResume() {
        super.onResume()
        scheduledCartView.dismiss()
    }

    override fun onCartClose() {
        ViewCompat.setNestedScrollingEnabled(nestedSv, true)
    }

    override fun onCartOpen() {
        ViewCompat.setNestedScrollingEnabled(nestedSv, false)
    }

    override fun onCreateNewScheduledSession() {
        ChooseQrOrScheduleBottomSheetFragment().show(supportFragmentManager, ChooseQrOrScheduleBottomSheetFragment.FRAGMENT_TAG)
    }

    override fun onScanResult(result: Int, bundle: Intent?) {
        if (result == Activity.RESULT_OK && bundle != null) {
            val data = bundle.getStringExtra(QRScannerActivity.KEY_QR_RESULT)
            scheduledSessionViewModel.createNewQrSession(data)
        } else if (result == Activity.RESULT_CANCELED) scheduledCartView.dismiss()
    }

    override fun onChooseQr() {
        supportFragmentManager.inTransaction {
            add(android.R.id.content, QRScannerWrapperFragment(), QRScannerWrapperFragment.FRAGMENT_TAG)
            addToBackStack(null)
        }
    }

    override fun onCancelChooseNewSession() {
        scheduledCartView.dismiss()
    }

    override fun onCancelScheduler() {
        if (cartViewModel.sessionPk != 0L) scheduledCartView.dismiss()
    }

    override fun onChooseSchedule() {
        SchedulerBottomSheetFragment().show(supportFragmentManager, SchedulerBottomSheetFragment.FRAGMENT_TAG)
    }

    override fun updateSessionTime() {
        onChooseSchedule()
    }

    override fun onSchedulerSet(selectedDate: Date, countPeople: Int) {
        scheduledSessionViewModel.syncScheduleInfo(selectedDate, countPeople, restaurantId)
    }

    override fun onBackPressed() {
        var result = supportFragmentManager.findFragmentByTag(QRScannerWrapperFragment.FRAGMENT_TAG)?.let { (it as BaseFragment).onBackPressed() }
                ?: false
        if (result) scheduledCartView.dismiss()
        if (!result) super.onBackPressed()
    }

    companion object {
        const val KEY_RESTAURANT_ID = "restaurant_profile.public.id"
        const val KEY_SESSION_ID = "session.new.id"
    }

    class PublicRestaurantProfileAdapter(fragmentActivity: FragmentActivity, val restaurantId: Long) : FragmentStateAdapter(fragmentActivity) {
        val tabs = listOf(RestaurantTab.MENU, RestaurantTab.INFO, RestaurantTab.REVIEWS)

        override fun getItemCount() = tabs.size

        override fun createFragment(position: Int): Fragment = when (tabs[position]) {
            RestaurantTab.MENU -> UserMenuFragment.newInstance(restaurantId)
            else -> BlankFragment.newInstance()
        }
    }

    enum class RestaurantTab {
        MENU, INFO, REVIEWS
    }
}

fun Context.openPublicRestaurantProfile(restaurantId: Long, sessionId: Long = 0) {
    startActivity(Intent(this, PublicRestaurantProfileActivity::class.java).apply {
        putExtra(PublicRestaurantProfileActivity.KEY_RESTAURANT_ID, restaurantId)
        putExtra(PublicRestaurantProfileActivity.KEY_SESSION_ID, sessionId)
    })
}

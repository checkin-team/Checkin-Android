package com.checkin.app.checkin.home.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.nearbyRestaurantModelHolder
import com.checkin.app.checkin.home.holders.LiveSessionTrackerAdapter
import com.checkin.app.checkin.home.holders.LiveSessionTrackerInteraction
import com.checkin.app.checkin.home.model.LiveSessionDetailModel
import com.checkin.app.checkin.home.model.SessionType
import com.checkin.app.checkin.home.model.TopAdBannerModel
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.home.viewmodels.LiveSessionViewModel
import com.checkin.app.checkin.menu.activities.ActiveSessionMenuActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.adBannerModelHolder
import com.checkin.app.checkin.misc.holders.shimmerModelHolder
import com.checkin.app.checkin.restaurant.activities.openPublicRestaurantProfile
import com.checkin.app.checkin.restaurant.models.RestaurantLocationModel
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.session.scheduled.activities.PreorderSessionDetailActivity
import com.checkin.app.checkin.session.scheduled.activities.QSRFoodReadyActivity
import com.checkin.app.checkin.session.scheduled.activities.QSRSessionDetailActivity
import com.checkin.app.checkin.utility.pass

class UserHomeFragment : BaseFragment(), LiveSessionTrackerInteraction {
    override val rootLayout = R.layout.fragment_user_home

    @BindView(R.id.epoxy_rv_home_banner)
    internal lateinit var epoxyRvHomeBanner: EpoxyRecyclerView
    @BindView(R.id.epoxy_rv_home_nearby_restaurants)
    internal lateinit var epoxyRvNearbyRestaurants: EpoxyRecyclerView
    @BindView(R.id.container_home_live_session)
    internal lateinit var containerLiveSession: ViewGroup
    @BindView(R.id.vp_home_session_live)
    internal lateinit var vpLiveSession: ViewPager2

    //  private lateinit var mRestAdapter: NearbyRestaurantAdapter
    private lateinit var mLiveSessionAdapter: LiveSessionTrackerAdapter

    private val mViewModel: HomeViewModel by activityViewModels()
    private val mLiveSessionViewModel: LiveSessionViewModel by activityViewModels()

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val message = MessageUtils.parseMessage(intent) ?: return
                val session = message.sessionDetail ?: return
                when (message.type) {
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_ACCEPTED,
                    MESSAGE_TYPE.USER_SCHEDULED_QSR_ACCEPTED
                    -> mLiveSessionViewModel.updateScheduledStatus(session.pk, ScheduledSessionStatus.ACCEPTED)
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_PREPARATION
                    -> mLiveSessionViewModel.updateScheduledStatus(session.pk, ScheduledSessionStatus.PREPARATION)
                    MESSAGE_TYPE.USER_SCHEDULED_CBYG_CHECKOUT,
                    MESSAGE_TYPE.USER_SCHEDULED_QSR_CHECKOUT
                    -> mLiveSessionViewModel.removeScheduledSession(session.pk)
                    MESSAGE_TYPE.USER_SCHEDULED_QSR_DONE
                    -> openQsrReady(session.pk)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshScreen(R.id.sr_home)
        enableDisableSwipeRefresh(true)

        epoxyRvHomeBanner.withModels {
            RemoteConfig.getListData<TopAdBannerModel>(RemoteConfig.Constants.HOME_TOP_BANNERS_AD)?.forEachIndexed { index, data ->
                adBannerModelHolder {
                    id("ad", index.toLong())
                    imageUrl(data.url)
                }
            }
        }
        mLiveSessionAdapter = LiveSessionTrackerAdapter(this)

        vpLiveSession.adapter = mLiveSessionAdapter

        epoxyRvNearbyRestaurants.withModels {
            mViewModel.nearbyRestaurants.value?.data?.forEachIndexed { id, data ->
                nearbyRestaurantModelHolder {
                    id(id)
                    restaurantModel(data)
                }
            } ?: shimmerModelHolder {
                id("sb1")
                withHomeRestaurantBannerLayout()
            }
        }
        epoxyRvNearbyRestaurants.requestModelBuild()

        mViewModel.nearbyRestaurants.observe(this, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.SUCCESS -> epoxyRvNearbyRestaurants.requestModelBuild()
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.liveSessionData.observe(this, Observer {
            it?.let {
                handleLoadingRefresh(it)
                when (it.status) {
                    Resource.Status.SUCCESS -> it.data?.let {
                        containerLiveSession.visibility = View.VISIBLE
                        mLiveSessionAdapter.updateData(it)
                    }
                    Resource.Status.LOADING -> startRefreshing()
                    Resource.Status.ERROR_NOT_FOUND -> {
                        containerLiveSession.visibility = View.GONE
                    }
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.fetchScheduledSessions()
        mLiveSessionViewModel.fetchLiveActiveSession()
    }

    override fun updateScreen() {
        super.updateScreen()
        mViewModel.fetchMissing()
        mViewModel.updateResults()
        mLiveSessionViewModel.updateResults()
    }

    override fun onOpenSessionDetails(session: LiveSessionDetailModel) {
        val intent = when (session.sessionType) {
            SessionType.DINING -> Intent(requireContext(), ActiveSessionActivity::class.java)
            SessionType.PREDINING -> PreorderSessionDetailActivity.withSessionIntent(requireContext(), session.pk)
            SessionType.QSR -> QSRSessionDetailActivity.withSessionIntent(requireContext(), session.pk)
        }
        startActivity(intent)
    }

    override fun onOpenRestaurantProfile(restaurant: RestaurantLocationModel) {
        requireContext().openPublicRestaurantProfile(restaurant.pk)
    }

    override fun onOpenRestaurantMenu(session: LiveSessionDetailModel) {
        when (session.sessionType) {
            SessionType.DINING -> ActiveSessionMenuActivity.openMenu(requireContext(), session.restaurant.pk)
            else -> onOpenRestaurantProfile(session.restaurant)
        }
    }

    fun openQsrReady(sessionPk: Long) {
        startActivity(QSRFoodReadyActivity.withSessionIntent(requireContext(), sessionPk))
    }

    override fun onResume() {
        super.onResume()
        val types = arrayOf(
                MESSAGE_TYPE.USER_SCHEDULED_CBYG_ACCEPTED, MESSAGE_TYPE.USER_SCHEDULED_CBYG_CHECKOUT,
                MESSAGE_TYPE.USER_SCHEDULED_CBYG_PREPARATION,
                MESSAGE_TYPE.USER_SCHEDULED_QSR_ACCEPTED, MESSAGE_TYPE.USER_SCHEDULED_QSR_CHECKOUT,
                MESSAGE_TYPE.USER_SCHEDULED_QSR_DONE
        )
        MessageUtils.registerLocalReceiver(requireContext(), receiver, *types)
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(requireContext(), receiver)
    }

    companion object {
        fun newInstance() = UserHomeFragment()
    }
}

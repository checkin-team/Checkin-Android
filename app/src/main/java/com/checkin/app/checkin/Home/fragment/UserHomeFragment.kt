package com.checkin.app.checkin.Home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Home.*
import com.checkin.app.checkin.Home.model.LiveSessionDetailModel
import com.checkin.app.checkin.Home.model.SessionType
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.RestaurantLocationModel
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity

class UserHomeFragment : BaseFragment(), LiveSessionTrackerInteraction {
    @BindView(R.id.rv_home_suggested_dishes)
    internal lateinit var rvSuggestedDishes: RecyclerView
    @BindView(R.id.rv_home_nearby_restaurants)
    internal lateinit var rvNearbyRestaurants: RecyclerView
    @BindView(R.id.container_home_live_session)
    internal lateinit var containerLiveSession: ViewGroup
    @BindView(R.id.vp_home_session_live)
    internal lateinit var vpLiveSession: ViewPager2

    private lateinit var mPopularDishAdapter: PopularDishesAdapter
    private lateinit var mRestAdapter: NearbyRestaurantAdapter
    private lateinit var mLiveSessionAdapter: LiveSessionTrackerAdapter

    private val mViewModel: HomeViewModel by activityViewModels()
    private val mLiveSessionViewModel: LiveSessionViewModel by viewModels()

    override val rootLayout: Int
        get() = R.layout.fragment_user_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enableDisableSwipeRefresh(true)

        mRestAdapter = NearbyRestaurantAdapter()
        mPopularDishAdapter = PopularDishesAdapter()
        mLiveSessionAdapter = LiveSessionTrackerAdapter(this)

        vpLiveSession.adapter = mLiveSessionAdapter

        rvNearbyRestaurants.layoutManager = LinearLayoutManager(context)
        rvNearbyRestaurants.adapter = mRestAdapter

        rvSuggestedDishes.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rvSuggestedDishes.adapter = mPopularDishAdapter
        rvNearbyRestaurants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                enableDisableSwipeRefresh(newState == RecyclerView.SCROLL_STATE_IDLE)
            }
        })

        mViewModel.nearbyRestaurants.observe(this, Observer {
            it?.let { listResource ->
                when (listResource.status) {
                    Resource.Status.SUCCESS -> listResource.data?.let(mRestAdapter::updateData)
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.liveSessionData.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> it.data?.let {
                        containerLiveSession.visibility = View.VISIBLE
                        mLiveSessionAdapter.updateData(it)
                    }
                    Resource.Status.ERROR_NOT_FOUND -> containerLiveSession.visibility = View.GONE
                    else -> pass
                }
            }
        })

        mLiveSessionViewModel.fetchScheduledSessions()
        mLiveSessionViewModel.fetchLiveActiveSession()
    }

    fun enableDisableSwipeRefresh(enable: Boolean) {
        (activity as? HomeActivity)?.enableDisableSwipeRefresh(enable)
    }

    override fun updateScreen() {
        super.updateScreen()
        mLiveSessionViewModel.updateResults()
    }

    override fun onOpenSessionDetails(session: LiveSessionDetailModel) {
        when (session.sessionType) {
            SessionType.DINING -> startActivity(Intent(requireContext(), ActiveSessionActivity::class.java))
            else -> pass
        }
    }

    override fun onOpenRestaurantProfile(restaurant: RestaurantLocationModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOpenRestaurantMenu(restaurant: RestaurantLocationModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance() = UserHomeFragment()
    }
}

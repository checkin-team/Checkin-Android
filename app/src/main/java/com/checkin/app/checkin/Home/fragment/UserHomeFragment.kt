package com.checkin.app.checkin.Home.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Home.*
import com.checkin.app.checkin.Misc.BaseFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass

class UserHomeFragment : BaseFragment() {
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

    private lateinit var mViewModel: HomeViewModel
    private lateinit var mLiveSessionViewModel: LiveSessionViewModel

    override fun getRootLayout() = R.layout.fragment_user_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        mLiveSessionViewModel = ViewModelProviders.of(this).get(LiveSessionViewModel::class.java)

        enableDisableSwipeRefresh(true)

        mRestAdapter = NearbyRestaurantAdapter()
        mPopularDishAdapter = PopularDishesAdapter()
        mLiveSessionAdapter = LiveSessionTrackerAdapter()

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
    }

    fun enableDisableSwipeRefresh(enable: Boolean) {
        (activity as? HomeActivity)?.enableDisableSwipeRefresh(enable)
    }

    override fun updateScreen() {
        super.updateScreen()
        mLiveSessionViewModel.updateResults()
    }

    companion object {
        fun newInstance() = UserHomeFragment()
    }
}

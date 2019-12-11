package com.checkin.app.checkin.Home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.Misc.BaseFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass

class UserHomeFragment : BaseFragment() {
    @BindView(R.id.rv_home_suggested_dishes)
    internal lateinit var rvSuggestedDishes: RecyclerView
    @BindView(R.id.rv_home_nearby_restaurants)
    internal lateinit var rvNearbyRestaurants: RecyclerView

    private lateinit var mPopularDishAdapter: PopularDishesAdapter
    private lateinit var mRestAdapter: NearbyRestaurantAdapter
    private lateinit var mViewModel: HomeViewModel

    override fun getRootLayout() = R.layout.fragment_user_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        enableDisableSwipeRefresh(true)

        mRestAdapter = NearbyRestaurantAdapter(context!!)

        rvNearbyRestaurants.layoutManager = LinearLayoutManager(context)
        rvNearbyRestaurants.adapter = mRestAdapter

        rvSuggestedDishes.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        mPopularDishAdapter = PopularDishesAdapter()
        rvSuggestedDishes.adapter = mPopularDishAdapter
        rvSuggestedDishes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    }

    fun enableDisableSwipeRefresh(enable: Boolean) {
        (activity as? HomeActivity)?.enableDisableSwipeRefresh(enable)
    }

    companion object {
        fun newInstance() = UserHomeFragment()
    }
}

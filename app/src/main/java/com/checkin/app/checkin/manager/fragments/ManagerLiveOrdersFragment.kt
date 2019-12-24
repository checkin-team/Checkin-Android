package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.restaurant.models.RestaurantServiceModel
import com.checkin.app.checkin.restaurant.models.RestaurantServiceType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ManagerLiveOrdersFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_shop_manager_live_orders

    @BindView(R.id.vp_manager_orders_fragment)
    internal lateinit var vpOrdersFragment: ViewPager2
    @BindView(R.id.tabs_manager_orders)
    internal lateinit var tabsFragment: TabLayout

    val viewModel: ManagerWorkViewModel by activityViewModels()
    val networkViewModel: BlockingNetworkViewModel by activityViewModels()
    private lateinit var tabAdapter: RestaurantOrdersServiceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabAdapter = RestaurantOrdersServiceAdapter(this, emptyList())
        vpOrdersFragment.adapter = tabAdapter
        TabLayoutMediator(tabsFragment, vpOrdersFragment) { tab, pos -> tab.text = tabAdapter.getTitle(pos) }.attach()
        tabsFragment.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(null) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vpOrdersFragment.currentItem = tab.position
            }
        })

        viewModel.restaurantService.observe(this, Observer {
            it?.let {
                networkViewModel.updateStatus(it)
                if (it.status == Resource.Status.SUCCESS) setupData(it.data!!)
            }
        })
    }

    private fun setupData(data: RestaurantServiceModel) {
        val tabs = mutableListOf<RestaurantOrdersFragmentType>()
        if (data.isQr) tabs.add(if (data.serviceType == RestaurantServiceType.DINEIN) RestaurantOrdersFragmentType.ACTIVE_SESSION else RestaurantOrdersFragmentType.MASTER_QR)
        if (data.isPreorder) tabs.add(RestaurantOrdersFragmentType.PRE_ORDER)
        tabAdapter.tabs = tabs
        tabAdapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ManagerLiveOrdersFragment()
    }

    class RestaurantOrdersServiceAdapter(fragment: Fragment, var tabs: List<RestaurantOrdersFragmentType>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = tabs.count()

        override fun createFragment(position: Int): Fragment = when (tabs[position]) {
            RestaurantOrdersFragmentType.ACTIVE_SESSION -> ManagerActiveSessionLiveTablesFragment.newInstance()
            RestaurantOrdersFragmentType.MASTER_QR -> ManagerQSRLiveOrdersFragment.newInstance()
            RestaurantOrdersFragmentType.PRE_ORDER -> ManagerScheduledLiveOrdersFragment.newInstance()
        }

        fun getTitle(pos: Int) = tabs[pos].title
    }
}

enum class RestaurantOrdersFragmentType(val title: String) {
    ACTIVE_SESSION("Live Orders"), MASTER_QR("Live Orders"), PRE_ORDER("Scheduled Orders");
}
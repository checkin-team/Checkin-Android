package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.referralModelHolder
import com.checkin.app.checkin.home.model.ReferralModel
import com.checkin.app.checkin.home.model.ReferralResultModel
import com.checkin.app.checkin.home.viewmodels.HomeViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [BaseFragment] subclass.
 * Use the [HomeReferralFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * mainly used to display referral information for stores nearby or a city
 */
class HomeReferralFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_home_referral

    @BindView(R.id.tv_home_referral_amount)
    internal lateinit var tvHomeReferralAmount: TextView

    @BindView(R.id.tv_home_referral_today)
    internal lateinit var tvHomeReferralToday: TextView

    @BindView(R.id.tabs_home_referral)
    internal lateinit var tabsHomeReferral: TabLayout

    @BindView(R.id.vp_home_referral)
    internal lateinit var vpHomeReferralList: ViewPager2

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        homeViewModel.referralLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    resource.data?.let {
                        tvHomeReferralAmount.text = it.balance.toString()
                        tvHomeReferralToday.text = it.formatToday()
                        setupReferrals(it)
                    }
                }
            }
        })
    }

    fun setupReferrals(referralResultModel: ReferralResultModel) {
        vpHomeReferralList.adapter = ReferralAdapter(requireActivity(), referralResultModel)
        val titleList = listOf("Offers", "Upcoming", "Rewards")
        TabLayoutMediator(tabsHomeReferral, vpHomeReferralList) { tab: TabLayout.Tab, positon: Int ->
            tab.text = titleList[positon]
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeReferralFragment()
    }

    class ReferralAdapter(fm: FragmentActivity, private val referralResultModel: ReferralResultModel) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReferralListFragment(referralResultModel.offersList)
                1 -> ReferralListFragment(referralResultModel.upcomingList)
                2 -> ReferralListFragment(referralResultModel.rewardsList)
                else -> ReferralListFragment(emptyList())
            }
        }

    }

    class ReferralListFragment(private val referralList: List<ReferralModel>) : BaseFragment() {
        override val rootLayout: Int = R.layout.fragment_home_referral_list

        @BindView(R.id.epoxy_rv_home_referral)
        internal lateinit var epoxyReferralList: EpoxyRecyclerView

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            epoxyReferralList.setHasFixedSize(true)
            epoxyReferralList.withModels {
                referralList.takeIf { it.isNotEmpty() }?.forEachIndexed { index, referralModel ->
                    referralModelHolder {
                        id(index)
                        referralModel(referralModel)
                    }
                }
            }
        }

    }
}

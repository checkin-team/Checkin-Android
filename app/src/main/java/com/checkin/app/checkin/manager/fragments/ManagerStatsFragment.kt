package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.holders.statsMenuAnalysisGroupRevenue
import com.checkin.app.checkin.manager.holders.statsMenuAnalysisItemRevenue
import com.checkin.app.checkin.manager.models.ManagerStatsModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.textSectionModelHolder
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty

class ManagerStatsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_shop_manager_statistics

    @BindView(R.id.epoxy_rv_manager_stats_menu_analysis)
    internal lateinit var epoxyRvMenuAnalysis: EpoxyRecyclerView

    @BindView(R.id.tv_manager_stats_revenue_live)
    internal lateinit var tvLiveCartRevenue: TextView

    @BindView(R.id.tv_manager_stats_revenue_yesterday)
    internal lateinit var tvYesterdayRevenue: TextView

    @BindView(R.id.tv_manager_stats_revenue_today)
    internal lateinit var tvDayRevenue: TextView

    @BindView(R.id.tv_manager_stats_revenue_week)
    internal lateinit var tvWeekRevenue: TextView

    @BindView(R.id.tv_manager_stats_orders_live)
    internal lateinit var tvLiveCartOrders: TextView

    @BindView(R.id.tv_manager_stats_orders_yesterday)
    internal lateinit var tvYesterdayOrders: TextView

    @BindView(R.id.tv_manager_stats_orders_today)
    internal lateinit var tvDayOrders: TextView

    @BindView(R.id.tv_manager_stats_orders_week)
    internal lateinit var tvWeekOrders: TextView

    @BindView(R.id.tv_manager_analysis_acceptance_time)
    internal lateinit var tvOrderAcceptanceTime: TextView

    @BindView(R.id.tv_manager_analysis_session_time)
    internal lateinit var tvSessionTime: TextView

    @BindView(R.id.tv_manager_analysis_serving_time)
    internal lateinit var tvServingTime: TextView

    private val mViewModel: ManagerWorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshScreen(R.id.sr_manager_stats)
        epoxyRvMenuAnalysis.withModels {
            mViewModel.restaurantStatistics.value?.data?.trendingItems.takeIf { it.isNotEmpty() }?.also {
                textSectionModelHolder {
                    withRedSmallLayout()
                    id("trending_items")
                    heading("TRENDING ITEMS IN LAST 30 DAYS")
                }
                it.forEachIndexed { idx, data ->
                    statsMenuAnalysisItemRevenue {
                        id(idx)
                        data(data)
                    }
                }
            }

            mViewModel.restaurantStatistics.value?.data?.poorItems.takeIf { it.isNotEmpty() }?.also {
                textSectionModelHolder {
                    withRedSmallLayout()
                    id("poor_items")
                    heading("LEAST SELLING ITEM IN LAST 30 DAYS")
                }
                it.forEachIndexed { idx, data ->
                    statsMenuAnalysisItemRevenue {
                        id(idx)
                        data(data)
                    }
                }
            }

            mViewModel.restaurantStatistics.value?.data?.trendingGroups.takeIf { it.isNotEmpty() }?.also {
                textSectionModelHolder {
                    withRedSmallLayout()
                    id("trending_groups")
                    heading("TOP GROUP IN LAST 30 DAYS")
                }
                it.forEachIndexed { idx, data ->
                    statsMenuAnalysisGroupRevenue {
                        id(idx)
                        index(idx + 1)
                        data(data)
                    }
                }
            }
        }

        mViewModel.fetchStatistics()
        mViewModel.restaurantStatistics.observe(viewLifecycleOwner, Observer {
            it?.let { input ->
                handleLoadingRefresh(input)
                if (input.status === Resource.Status.SUCCESS && input.data != null) {
                    setupData(input.data)
                }
            }
        })
    }

    private fun setupData(data: ManagerStatsModel) {
        tvLiveCartOrders.text = "%.0f Orders".format(data.countOrders.live)
        tvDayOrders.text = "%.0f Orders".format(data.countOrders.day)
        tvYesterdayOrders.text = "%.0f Orders".format(data.countOrders.yesterday)
        tvWeekOrders.text = "%.0f Orders".format(data.countOrders.week)
        tvLiveCartRevenue.text = Utils.formatCurrencyAmount(context, data.revenue.live)
        tvDayRevenue.text = Utils.formatCurrencyAmount(context, data.revenue.day)
        tvYesterdayRevenue.text = Utils.formatCurrencyAmount(context, data.revenue.yesterday)
        tvWeekRevenue.text = Utils.formatCurrencyAmount(context, data.revenue.week)

        tvSessionTime.text = "%s Session time".format(data.formatAvgSessionTime())
        tvServingTime.text = "%s Serving time".format(data.formatAvgServingTime())
        tvOrderAcceptanceTime.text = "%s Order Accepting time".format(data.formatOrderAcceptingTime())
    }

    override fun updateScreen() {
        super.updateScreen()
        mViewModel.fetchStatistics()
    }

    companion object {
        fun newInstance(): ManagerStatsFragment = ManagerStatsFragment()
    }
}
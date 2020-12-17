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
import com.checkin.app.checkin.manager.adapters.ManagerStatsOrderAdapter
import com.checkin.app.checkin.manager.holders.managerStatsModelHeadingHolder
import com.checkin.app.checkin.manager.holders.managerStatsModelPoorOrderHolder
import com.checkin.app.checkin.manager.holders.managerStatsModelTrendingHolder
import com.checkin.app.checkin.manager.holders.managerStatsTrendingGroupsHolder
import com.checkin.app.checkin.manager.models.ManagerStatsModel
import com.checkin.app.checkin.manager.models.getHeading

import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty
import java.util.*

class ManagerStatsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_shop_manager_statistics

    @BindView(R.id.rv_manager_stats_trending_orders)
    internal lateinit var rvTrendingOrders: EpoxyRecyclerView

    @BindView(R.id.tv_manager_stats_revenue_day)
    internal lateinit var tvDayRevenue: TextView

    @BindView(R.id.tv_manager_stats_revenue_week)
    internal lateinit var tvWeekRevenue: TextView

    @BindView(R.id.tv_manager_stats_orders_day)
    internal lateinit var tvDayOrders: TextView

    @BindView(R.id.tv_manager_stats_orders_week)
    internal lateinit var tvWeekOrders: TextView

    @BindView(R.id.tv_manager_stats_session_time)
    internal lateinit var tvSessionTime: TextView

    @BindView(R.id.tv_manager_stats_serving_time)
    internal lateinit var tvServingTime: TextView

    private val mAdapter: ManagerStatsOrderAdapter by lazy { ManagerStatsOrderAdapter() }
    private val mViewModel: ManagerWorkViewModel by activityViewModels()
    val simplemodelheading = getHeading()

    val trendingOrdersmanager = mutableListOf<ManagerStatsModel.TrendingOrder>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshScreen(R.id.sr_manager_stats)
        rvTrendingOrders.withModels {

            simplemodelheading.forEachIndexed { pos, model ->
                managerStatsModelHeadingHolder {
                    id(model.type)
                    data(model)
                }
                when (model.header) {
                    "TrendingItems" ->
                        mViewModel.restaurantStatistics.value?.data?.trendingOrders.takeIf { it.isNotEmpty() }?.forEach {

                            managerStatsModelTrendingHolder {
                                id(hashCode())
                                data(it)
                            }
                        }
                    "PoorItems" ->

                        mViewModel.restaurantStatistics.value?.data?.poorOrders.takeIf { it.isNotEmpty() }?.forEach {

                            managerStatsModelPoorOrderHolder {
                                id(hashCode())
                                data(it)
                            }
                        }


                    "TrendingGroups" ->
                        mViewModel.restaurantStatistics.value?.data?.trendingGroups.takeIf { it.isNotEmpty() }?.forEach {

                            managerStatsTrendingGroupsHolder {
                                id(hashCode())
                                data(it)
                            }
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
        tvDayOrders.text = String.format(Locale.ENGLISH, "%s orders", data.dayOrdersCount)
        tvWeekOrders.text = String.format(Locale.ENGLISH, "%s orders", data.weekOrdersCount)
        tvDayRevenue.text = Utils.formatCurrencyAmount(context, data.dayRevenue)
        tvWeekRevenue.text = Utils.formatCurrencyAmount(context, data.weekRevenue)
        tvSessionTime.text = data.formatAvgSessionTime()
        tvServingTime.text = data.formatAvgServingTime()
    }

    override fun updateScreen() {
        super.updateScreen()
        mViewModel.fetchStatistics()
    }

    companion object {
        fun newInstance(): ManagerStatsFragment = ManagerStatsFragment()
    }
}
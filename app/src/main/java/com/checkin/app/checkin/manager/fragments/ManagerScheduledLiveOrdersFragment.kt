package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.manager.activities.ManagerScheduledSessionDetailActivity
import com.checkin.app.checkin.manager.controllers.PreorderTablesController
import com.checkin.app.checkin.manager.holders.PreorderTableInteraction
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLivePreOrdersViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerScheduledLiveOrdersFragment : BaseFragment(), PreorderTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_live_preorders

    @BindView(R.id.epoxy_rv_manager_live_preorders)
    internal lateinit var epoxyRvLivePreorders: EpoxyRecyclerView

    val preorderController = PreorderTablesController(this)
    val viewModel: ManagerLivePreOrdersViewModel by viewModels()
    val workViewModel: ManagerWorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvLivePreorders.setController(preorderController)
        viewModel.fetchPreOrders(workViewModel.shopPk)

        viewModel.preOrdersData.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> updateList(it.data!!)
                }
            }
        })
    }

    private fun updateList(data: List<ShopScheduledSessionModel>) {
        preorderController.sessions = data
    }

    override fun onClickNewTable(data: ShopScheduledSessionModel) {
        ManagerScheduledSessionDetailActivity.startScheduledSessionDetailActivity(requireContext(), data.pk)
    }

    override fun onClickPreparationTable(data: ShopScheduledSessionModel) {
    }

    override fun onMarkFoodServed(data: ShopScheduledSessionModel) {
    }

    override fun onClickUpcomingTable(data: ShopScheduledSessionModel) {
    }

    companion object {
        fun newInstance() = ManagerScheduledLiveOrdersFragment()
    }
}
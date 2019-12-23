package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.manager.activities.ManagerQSRDetailActivity
import com.checkin.app.checkin.manager.controllers.QSRTablesController
import com.checkin.app.checkin.manager.holders.QSRTableInteraction
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment

class ManagerQSRLiveOrdersFragment : BaseFragment(), QSRTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_qsr_live_orders

    @BindView(R.id.epoxy_rv_manager_live_qsr_orders)
    internal lateinit var epoxyRvTables: EpoxyRecyclerView

    val qsrTablesController = QSRTablesController(this)
    val viewModel: ManagerLiveScheduledViewModel by activityViewModels()
    val workViewModel: ManagerWorkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvTables.setController(qsrTablesController)
        if (viewModel.shopPk == 0L) viewModel.fetchScheduledSessions(workViewModel.shopPk)

        viewModel.qsrOrdersData.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> updateList(it.data!!)
                    else -> pass
                }
            }
        })
        viewModel.doneData.observe(this, Observer {
            // Handle complex logic Checkedout null or not
        })
    }

    private fun updateList(data: List<ShopScheduledSessionModel>) {
        qsrTablesController.sessions = data
    }

    override fun onClickNewTable(data: ShopScheduledSessionModel) {
        ManagerQSRDetailActivity.startQsrSessionDetailActivity(requireContext(), data.pk)
    }

    override fun onClickPreparationTable(data: ShopScheduledSessionModel) {
        ManagerQSRDetailActivity.startQsrSessionDetailActivity(requireContext(), data.pk)
    }

    override fun onMarkFoodReady(data: ShopScheduledSessionModel) {
        viewModel.markSessionDone(data.pk)
    }

    override fun onMarkSessionEnd(data: ShopScheduledSessionModel) {
        viewModel.markSessionDone(data.pk)
    }

    override fun onClickEndTable(data: ShopScheduledSessionModel) {
        ManagerQSRDetailActivity.startQsrSessionDetailActivity(requireContext(), data.pk)
    }

    companion object {
        fun newInstance() = ManagerQSRLiveOrdersFragment()
    }
}
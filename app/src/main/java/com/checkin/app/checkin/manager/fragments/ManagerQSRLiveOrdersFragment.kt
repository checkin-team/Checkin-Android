package com.checkin.app.checkin.manager.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.activities.ManagerQSRDetailActivity
import com.checkin.app.checkin.manager.controllers.QSRTablesController
import com.checkin.app.checkin.manager.holders.QSRTableInteraction
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.pass

class ManagerQSRLiveOrdersFragment : BaseFragment(), QSRTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_qsr_live_orders

    @BindView(R.id.epoxy_rv_manager_live_qsr_orders)
    internal lateinit var epoxyRvTables: EpoxyRecyclerView

    private val qsrTablesController = QSRTablesController(this)
    private val viewModel: ManagerLiveScheduledViewModel by activityViewModels()
    private val workViewModel: ManagerWorkViewModel by activityViewModels()

    private val mReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val message = MessageUtils.parseMessage(intent) ?: return
                val session = message.sessionDetail ?: return
                val shop = message.shopDetail
                if (shop != null && shop.pk != viewModel.shopPk) return
                when (message.type) {
                    MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_NEW_PAID -> viewModel.updateResults()
                    MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_CANCELLED -> viewModel.removeSession(session.pk)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvTables.setController(qsrTablesController)
        if (viewModel.shopPk == 0L) viewModel.fetchScheduledSessions(workViewModel.shopPk)

        viewModel.qsrOrdersData.observe(this, Observer {
            it?.let {
                (parentFragment as? BaseFragment)?.handleLoadingRefresh(it)
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
        startActivityForResult(ManagerQSRDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun onClickPreparationTable(data: ShopScheduledSessionModel) {
        startActivityForResult(ManagerQSRDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun onMarkFoodReady(data: ShopScheduledSessionModel) {
        viewModel.markSessionDone(data.pk)
    }

    override fun onMarkSessionEnd(data: ShopScheduledSessionModel) {
        viewModel.markSessionDone(data.pk)
    }

    override fun onClickEndTable(data: ShopScheduledSessionModel) {
        startActivityForResult(ManagerQSRDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_INTENT_RESULT -> if (resultCode == FragmentActivity.RESULT_OK) viewModel.updateResults()
        }
    }

    override fun updateScreen() {
        viewModel.updateResults()
    }

    override fun onResume() {
        super.onResume()
        MessageUtils.registerLocalReceiver(
                requireContext(), mReceiver, MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_NEW_PAID,
                MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_CANCELLED
        )
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver)
    }

    companion object {
        private const val RC_INTENT_RESULT = 190

        fun newInstance() = ManagerQSRLiveOrdersFragment()
    }
}
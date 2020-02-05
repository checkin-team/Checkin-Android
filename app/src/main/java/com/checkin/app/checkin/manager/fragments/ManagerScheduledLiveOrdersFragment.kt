package com.checkin.app.checkin.manager.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.activities.ManagerPreOrderDetailActivity
import com.checkin.app.checkin.manager.controllers.PreorderTablesController
import com.checkin.app.checkin.manager.holders.PreorderTableInteraction
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.ScheduledSessionStatus
import com.checkin.app.checkin.utility.isNotEmpty
import com.checkin.app.checkin.utility.pass

class ManagerScheduledLiveOrdersFragment : BaseFragment(), PreorderTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_live_preorders

    @BindView(R.id.epoxy_rv_manager_live_preorders)
    internal lateinit var epoxyRvLivePreorders: EpoxyRecyclerView
    @BindView(R.id.ll_no_orders)
    internal lateinit var llNoLiveOrders: LinearLayout

    private val preorderController = PreorderTablesController(this)
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
                    MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_NEW_PAID -> viewModel.updateResults()
                    MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_CANCELLED, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_AUTO_CANCELLED -> viewModel.removeSession(session.pk)
                    MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_PREPARATION_START -> viewModel.updateSessionStatus(session.pk, ScheduledSessionStatus.PREPARATION)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvLivePreorders.setController(preorderController)
        if (viewModel.shopPk == 0L) viewModel.fetchScheduledSessions(workViewModel.shopPk)

        viewModel.preOrdersData.observe(this, Observer {
            it?.let {
                (parentFragment as? BaseFragment)?.handleLoadingRefresh(it)
                when (it.status) {
                    Resource.Status.SUCCESS -> updateList(it.data!!)
                    else -> pass
                }
            }
        })
        viewModel.doneData.observe(this, Observer {
        })
    }

    private fun updateUi(data: List<ShopScheduledSessionModel>) {
        if (data.isNotEmpty()) {
            epoxyRvLivePreorders.visibility = View.VISIBLE
            llNoLiveOrders.visibility = View.GONE
        } else {
            epoxyRvLivePreorders.visibility = View.GONE
            llNoLiveOrders.visibility = View.VISIBLE
        }
    }

    private fun updateList(data: List<ShopScheduledSessionModel>) {
        updateUi(data)
        preorderController.sessions = data
    }


    override fun onClickNewTable(data: ShopScheduledSessionModel) {
        startActivityForResult(ManagerPreOrderDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun onClickPreparationTable(data: ShopScheduledSessionModel) {
        startActivityForResult(ManagerPreOrderDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun onMarkFoodServed(data: ShopScheduledSessionModel) {
        viewModel.markSessionDone(data.pk)
    }

    override fun onClickUpcomingTable(data: ShopScheduledSessionModel) {
        startActivityForResult(ManagerPreOrderDetailActivity.withSessionIntent(requireContext(), data.pk), RC_INTENT_RESULT)
    }

    override fun updateScreen() {
        viewModel.updateResults()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_INTENT_RESULT -> if (resultCode == FragmentActivity.RESULT_OK) viewModel.updateResults()
        }
    }

    override fun onResume() {
        super.onResume()
        MessageUtils.registerLocalReceiver(
                requireContext(), mReceiver, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_NEW_PAID, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_AUTO_CANCELLED,
                MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_CANCELLED, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_PREPARATION_START
        )
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver)
    }

    companion object {
        private const val RC_INTENT_RESULT = 190

        fun newInstance() = ManagerScheduledLiveOrdersFragment()
    }
}
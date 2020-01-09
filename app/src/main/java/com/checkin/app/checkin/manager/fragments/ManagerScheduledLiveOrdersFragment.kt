package com.checkin.app.checkin.manager.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE
import com.checkin.app.checkin.Data.Message.MessageUtils
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.manager.activities.ManagerPreOrderDetailActivity
import com.checkin.app.checkin.manager.controllers.PreorderTablesController
import com.checkin.app.checkin.manager.holders.PreorderTableInteraction
import com.checkin.app.checkin.manager.models.ShopScheduledSessionModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLiveScheduledViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.ScheduledSessionStatus

class ManagerScheduledLiveOrdersFragment : BaseFragment(), PreorderTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_live_preorders

    @BindView(R.id.epoxy_rv_manager_live_preorders)
    internal lateinit var epoxyRvLivePreorders: EpoxyRecyclerView

    private val preorderController = PreorderTablesController(this)
    private val viewModel: ManagerLiveScheduledViewModel by activityViewModels()
    private val workViewModel: ManagerWorkViewModel by activityViewModels()

    private val mReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = MessageUtils.parseMessage(intent) ?: return
                val session = message.sessionDetail ?: return
                val shop = message.shopDetail
                if (shop != null && shop.pk != viewModel.shopPk) return
                when (message.type) {
                    MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_NEW_PAID -> viewModel.updateResults()
                    MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_CANCELLED -> viewModel.removeSession(session.pk)
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
                when (it.status) {
                    Resource.Status.SUCCESS -> updateList(it.data!!)
                    else -> pass
                }
            }
        })
        viewModel.doneData.observe(this, Observer {
        })
    }

    private fun updateList(data: List<ShopScheduledSessionModel>) {
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
                requireContext(), mReceiver, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_NEW_PAID,
                MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_CANCELLED, MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_PREPARATION_START
        )
    }

    companion object {
        private const val RC_INTENT_RESULT = 190

        fun newInstance() = ManagerScheduledLiveOrdersFragment()
    }
}
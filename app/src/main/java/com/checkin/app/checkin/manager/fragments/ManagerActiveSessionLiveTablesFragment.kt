package com.checkin.app.checkin.manager.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageObjectModel
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.activities.ManagerSessionActivity
import com.checkin.app.checkin.manager.activities.ManagerSessionInvoiceActivity
import com.checkin.app.checkin.manager.adapters.ManagerWorkTableAdapter
import com.checkin.app.checkin.manager.adapters.ManagerWorkTableAdapter.ManagerTableInteraction
import com.checkin.app.checkin.manager.viewmodels.ManagerASLiveTablesViewModel
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.session.models.TableSessionModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty

class ManagerActiveSessionLiveTablesFragment : BaseFragment(), ManagerTableInteraction {
    override val rootLayout: Int = R.layout.fragment_manager_active_session_live_tables

    @BindView(R.id.rv_shop_manager_table)
    internal lateinit var rvShopManagerTable: RecyclerView
    @BindView(R.id.ll_no_orders)
    internal lateinit var llNoLiveOrders: LinearLayout

    private val mAdapter: ManagerWorkTableAdapter = ManagerWorkTableAdapter(this)
    private val workViewModel: ManagerWorkViewModel by activityViewModels()
    private val mViewModel: ManagerASLiveTablesViewModel by viewModels()

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = MessageUtils.parseMessage(intent) ?: return
            val shop = message.shopDetail
            if (shop != null && shop.pk != mViewModel.shopPk) return
            val sessionData = message.sessionDetail ?: return
            when (message.type) {
                MESSAGE_TYPE.MANAGER_SESSION_NEW -> {
                    val qrPk = message.`object`?.pk ?: return
                    val tableName = message.rawData?.sessionTableName ?: return
                    val actor = message.actor ?: return
                    val eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief
                            ?: return).apply {
                        type = SessionChatModel.CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN
                    }
                    val tableSessionModel = TableSessionModel(sessionData.pk, null, eventModel)
                    val tableModel = RestaurantTableModel(qrPk, tableName, tableSessionModel)
                    if (actor.type == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        tableModel.tableSession?.host = message.actor.briefModel
                    }
                    this@ManagerActiveSessionLiveTablesFragment.addTable(tableModel)
                }
                MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER, MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE,
                MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN, MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST -> {
                    val eventModel = EventBriefModel.getFromManagerEventModel(message.rawData?.sessionEventBrief
                            ?: return)
                    this@ManagerActiveSessionLiveTablesFragment.updateSessionEventCount(sessionData.pk, eventModel)
                }
                MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED -> {
                    val user = message.`object`?.briefModel ?: return
                    this@ManagerActiveSessionLiveTablesFragment.updateSessionHost(sessionData.pk, user)
                }
                MESSAGE_TYPE.MANAGER_SESSION_END -> this@ManagerActiveSessionLiveTablesFragment.removeTable(sessionData.pk)
            }
        }
    }

    private fun updateUi(data: List<RestaurantTableModel>) {
        if (data.isNotEmpty()) {
            mAdapter.setRestaurantTableList(data)
            rvShopManagerTable.visibility = View.VISIBLE
            llNoLiveOrders.visibility = View.GONE
        } else {
            rvShopManagerTable.visibility = View.GONE
            llNoLiveOrders.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvShopManagerTable.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvShopManagerTable.adapter = mAdapter
        mViewModel.activeTables.observe(viewLifecycleOwner, Observer {
            it?.let { input ->
                if (input.status === Resource.Status.SUCCESS && input.data != null) {
                    updateUi(input.data)
                } else if (input.status !== Resource.Status.LOADING) {
                    Utils.toast(requireContext(), input.message)
                }
            }
        })
        mViewModel.checkoutData.observe(viewLifecycleOwner, Observer {
            it?.also { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    Utils.toast(requireContext(), resource.data.message)
                    if (resource.data.isCheckout) mViewModel.updateRemoveTable(resource.data.sessionPk) else mViewModel.updateResults()
                } else if (resource.status !== Resource.Status.LOADING) {
                    Utils.toast(requireContext(), resource.message)
                }
            }
        })
        mViewModel.sessionInitiated.observe(viewLifecycleOwner, Observer {
            it?.let { qrResultModelResource ->
                if (qrResultModelResource.status === Resource.Status.SUCCESS && qrResultModelResource.data != null) {
                    mViewModel.fetchActiveTables(mViewModel.shopPk)
                } else {
                    Utils.toast(requireContext(), qrResultModelResource.message)
                }
            }
        })
        mViewModel.inactiveTables.observe(viewLifecycleOwner, Observer { })

        mViewModel.fetchActiveTables(workViewModel.shopPk)
    }

    override fun onClickTable(tableModel: RestaurantTableModel) {
        if (tableModel.tableSession != null) {
            val intent = Intent(context, ManagerSessionActivity::class.java)
            intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.tableSession!!.pk)
                    .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.shopPk)
            startActivity(intent)
            val pos = mViewModel.getTablePositionWithPk(tableModel.tableSession!!.pk)
            tableModel.resetEvents()
            mAdapter.updateSession(pos)
        }
    }

    override fun onClickBill(tableModel: RestaurantTableModel) {
        val intent = Intent(context, ManagerSessionInvoiceActivity::class.java)
        intent.putExtra(ManagerSessionInvoiceActivity.TABLE_NAME, tableModel.table)
                .putExtra(ManagerSessionInvoiceActivity.KEY_SESSION, tableModel.tableSession!!.pk)
                .putExtra(ManagerSessionInvoiceActivity.IS_REQUESTED_CHECKOUT, tableModel.tableSession!!.isRequestedCheckout)
        startActivity(intent)
    }

    override fun onMarkSessionDone(tableModel: RestaurantTableModel) {
        if (tableModel.tableSession != null) {
            mViewModel.markSessionDone(tableModel.tableSession!!.pk)
            mViewModel.updateResults()
        }
    }

    @OnClick(R.id.im_manager_initiate_session)
    fun onClickInitiate() {
        if (mViewModel.inactiveTables.value?.data.isNotEmpty()) ManagerInactiveTableBottomSheetFragment.forNewTable().show(childFragmentManager, ManagerInactiveTableBottomSheetFragment.FRAGMENT_SHOW_TAG)
        else Utils.toast(requireContext(), "No table is free.")
    }

    // region UI-Update
    private fun addTable(tableModel: RestaurantTableModel) {
        tableModel.addEvent(tableModel.tableSession?.event ?: return)
        mViewModel.addRestaurantTable(tableModel)
        tableModel.addToDb()
    }

    private fun updateSessionEventCount(sessionPk: Long, event: EventBriefModel) {
        mViewModel.updateTable(sessionPk, event)
    }

    private fun updateSessionHost(sessionPk: Long, user: BriefModel) {
        val pos: Int = mViewModel.getTablePositionWithPk(sessionPk)
        val table = mViewModel.getTableWithPosition(pos)
        if (table != null) {
            val tableSessionModel = table.tableSession
            if (tableSessionModel != null) {
                tableSessionModel.host = user
            }
            mAdapter.updateSession(pos)
        }
    }

    private fun removeTable(sessionPk: Long) {
        mViewModel.updateRemoveTable(sessionPk)
    }
    // endregion

    override fun onResume() {
        super.onResume()
        val types = arrayOf(MESSAGE_TYPE.MANAGER_SESSION_NEW, MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER, MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE, MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST, MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN, MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.MANAGER_SESSION_END)
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, *types)
        //        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver)
    }

    override fun updateScreen() {
        mViewModel.updateResults()
    }

    companion object {
        fun newInstance(): ManagerActiveSessionLiveTablesFragment {
            return ManagerActiveSessionLiveTablesFragment()
        }
    }
}
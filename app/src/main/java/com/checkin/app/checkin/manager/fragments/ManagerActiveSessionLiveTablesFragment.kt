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
import com.checkin.app.checkin.Data.Message.MessageModel
import com.checkin.app.checkin.Data.Message.MessageObjectModel
import com.checkin.app.checkin.Data.Message.MessageUtils
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.isNotEmpty
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
import java.util.*

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
            val eventModel: EventBriefModel
            val user: BriefModel
            when (message.type) {
                MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW -> {
                    val tableName = message.rawData.sessionTableName
                    eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief)
                    eventModel.type = SessionChatModel.CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN
                    val sessionData = message.sessionDetail!!
                    val tableSessionModel = TableSessionModel(sessionData.pk, null, eventModel)
                    tableSessionModel.created = Calendar.getInstance().time
                    val tableModel = RestaurantTableModel(message.getObject().pk, tableName, tableSessionModel)
                    if (message.actor.type == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.actor.briefModel
                        if (tableModel.tableSession != null) {
                            tableModel.tableSession!!.host = user
                        }
                    }
                    this@ManagerActiveSessionLiveTablesFragment.addTable(tableModel)
                }
                MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST -> {
                    eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief)
                    this@ManagerActiveSessionLiveTablesFragment.updateSessionEventCount(message.target.pk, eventModel)
                }
                MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED -> {
                    user = message.getObject().briefModel
                    this@ManagerActiveSessionLiveTablesFragment.updateSessionHost(message.target.pk, user)
                }
                MessageModel.MESSAGE_TYPE.MANAGER_SESSION_END -> this@ManagerActiveSessionLiveTablesFragment.removeTable(message.sessionDetail!!.pk)
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
        mViewModel.activeTables.observe(this, Observer {
            it?.let { input ->
                if (input.status === Resource.Status.SUCCESS && input.data != null) {
                    updateUi(input.data)
                } else if (input.status !== Resource.Status.LOADING) {
                    Utils.toast(requireContext(), input.message)
                }
            }
        })
        mViewModel.checkoutData.observe(this, Observer {
            it?.let { resource ->
                if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                    Utils.toast(requireContext(), resource.data.message)
                    if (resource.data.isCheckout) mViewModel.updateRemoveTable(resource.data.sessionPk) else mViewModel.updateResults()
                } else if (resource.status !== Resource.Status.LOADING) {
                    Utils.toast(requireContext(), resource.message)
                }
            }
        })
        mViewModel.sessionInitiated.observe(this, Observer {
            it?.let { qrResultModelResource ->
                if (qrResultModelResource.status === Resource.Status.SUCCESS && qrResultModelResource.data != null) {
                    mViewModel.fetchActiveTables(mViewModel.shopPk)
                } else {
                    Utils.toast(requireContext(), qrResultModelResource.message)
                }
            }
        })

        mViewModel.fetchActiveTables(workViewModel.shopPk)
    }

    override fun onClickTable(tableModel: RestaurantTableModel) {
        if (tableModel.tableSession != null) {
            val intent = Intent(context, ManagerSessionActivity::class.java)
            intent.putExtra(ManagerSessionActivity.KEY_SESSION_PK, tableModel.tableSession!!.pk)
                    .putExtra(ManagerSessionActivity.KEY_SHOP_PK, mViewModel.shopPk)
            startActivity(intent)
            val pos = mViewModel.getTablePositionWithPk(tableModel.tableSession!!.pk)
            tableModel.eventCount = 0
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
        if (mViewModel.inactiveTables.value?.data.isNotEmpty()) ManagerInactiveTableBottomSheetFragment().show(childFragmentManager, null)
        else Utils.toast(requireContext(), "No table is free.")
    }

    // region UI-Update
    private fun addTable(tableModel: RestaurantTableModel) {
        tableModel.eventCount = 1
        mViewModel.addRestaurantTable(tableModel)
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
        val types = arrayOf(MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_NEW_ORDER, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_SERVICE, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_HOST_ASSIGNED, MessageModel.MESSAGE_TYPE.MANAGER_SESSION_END)
        MessageUtils.registerLocalReceiver(requireContext(), mReceiver, *types)
        //        MessageUtils.dismissNotification(this, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, mViewModel.getSessionPk());
    }

    override fun onPause() {
        super.onPause()
        MessageUtils.unregisterLocalReceiver(requireContext(), mReceiver)
    }


    companion object {
        fun newInstance(): ManagerActiveSessionLiveTablesFragment {
            return ManagerActiveSessionLiveTablesFragment()
        }
    }
}
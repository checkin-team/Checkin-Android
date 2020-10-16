package com.checkin.app.checkin.cook.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.cook.activities.CookSessionActivity
import com.checkin.app.checkin.cook.adapters.CookWorkTableAdapter
import com.checkin.app.checkin.cook.adapters.CookWorkTableAdapter.CookTableInteraction
import com.checkin.app.checkin.cook.viewmodels.CookWorkViewModel
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageObjectModel
import com.checkin.app.checkin.data.notifications.MessageUtils.parseMessage
import com.checkin.app.checkin.data.notifications.MessageUtils.registerLocalReceiver
import com.checkin.app.checkin.data.notifications.MessageUtils.unregisterLocalReceiver
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.session.models.TableSessionModel
import com.checkin.app.checkin.utility.Utils

class CookTablesFragment : BaseFragment(), CookTableInteraction {
    override val rootLayout: Int = R.layout.fragment_shop_cook_tables

    @BindView(R.id.rv_shop_cook_tables)
    internal lateinit var rvShopManagerTable: RecyclerView
    @BindView(R.id.ll_no_orders)
    internal lateinit var llNoLiveOrders: LinearLayout

    private val mAdapter: CookWorkTableAdapter = CookWorkTableAdapter(this)
    private val mViewModel: CookWorkViewModel by activityViewModels()

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = parseMessage(intent)
                    ?: return
            val shop = message.shopDetail
            if (shop != null && shop.pk != mViewModel.shopPk) return
            val user: BriefModel
            when (message.type) {
                MESSAGE_TYPE.COOK_SESSION_NEW -> {
                    val qrPk = message.rawData?.sessionQRId ?: return
                    val tableName = message.rawData.sessionTableName ?: return
                    val actor = message.actor ?: return
                    val eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief
                            ?: return).apply {
                        type = SessionChatModel.CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN
                    }
                    val sessionData = message.sessionDetail ?: return
                    val tableSessionModel = TableSessionModel(sessionData.pk, null, eventModel)
                    val tableModel = RestaurantTableModel(qrPk, tableName, tableSessionModel).apply {
                        addEvent(eventModel)
                        if (actor.type == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                            tableSession?.host = message.actor.briefModel
                        }
                    }
                    addTable(tableModel)
                }
                MESSAGE_TYPE.COOK_SESSION_NEW_ORDER -> updateSessionNewOrder(message.target!!.pk, message.`object`!!.pk)
                MESSAGE_TYPE.COOK_SESSION_HOST_ASSIGNED -> {
                    user = message.`object`!!.briefModel
                    updateSessionHost(message.target!!.pk, user)
                }
                MESSAGE_TYPE.COOK_SESSION_SWITCH_TABLE -> mViewModel.fetchActiveTables(mViewModel.shopPk)
                MESSAGE_TYPE.COOK_SESSION_END -> {
                    removeTable(message.sessionDetail?.pk ?: return)
                }
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
        rvShopManagerTable.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rvShopManagerTable.adapter = mAdapter
        }
        mViewModel.activeTables.observe(viewLifecycleOwner, Observer {
            it?.let { input ->
                if (input.data != null) updateUi(input.data)
                else if (input.status !== Resource.Status.LOADING) {
                    Utils.toast(requireContext(), input.message)
                }
            }
        })
    }

    // region UI-Update
    private fun addTable(tableModel: RestaurantTableModel) {
        mViewModel.addRestaurantTable(tableModel)
        tableModel.addToDb()
    }

    private fun updateSessionNewOrder(sessionPk: Long, newOrderPk: Long) {
        mViewModel.updateTable(sessionPk, newOrderPk)
    }

    private fun updateSessionHost(sessionPk: Long, user: BriefModel) {
        val pos = mViewModel.getTablePositionWithPk(sessionPk)
        val table = mViewModel.getTableWithPosition(pos) ?: return
        table.tableSession?.host = user
        mAdapter.updateSession(pos)
    }

    private fun removeTable(sessionPk: Long) {
        mViewModel.updateRemoveTable(sessionPk)
    }

    // endregion
    override fun onResume() {
        super.onResume()
        val types = arrayOf(MESSAGE_TYPE.COOK_SESSION_NEW, MESSAGE_TYPE.COOK_SESSION_NEW_ORDER, MESSAGE_TYPE.COOK_SESSION_UPDATE_ORDER, MESSAGE_TYPE.COOK_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.COOK_SESSION_SWITCH_TABLE, MESSAGE_TYPE.COOK_SESSION_END)
        registerLocalReceiver(requireContext(), mReceiver, *types)
        mViewModel.updateResults()
    }

    override fun onPause() {
        super.onPause()
        unregisterLocalReceiver(requireContext(), mReceiver)
    }

    fun openTableDetails(tableModel: RestaurantTableModel) {
        val intent = Intent(context, CookSessionActivity::class.java)
        val sessionPk = tableModel.sessionPk ?: return
        intent.putExtra(CookSessionActivity.KEY_SESSION_PK, sessionPk)
                .putExtra(CookSessionActivity.KEY_SHOP_PK, mViewModel.shopPk)
        startActivity(intent)
        val pos = mViewModel.getTablePositionWithPk(sessionPk).takeIf { it != -1 } ?: return
        tableModel.resetEvents()
        mAdapter.updateSession(pos)
    }

    override fun onClickTable(tableModel: RestaurantTableModel) = openTableDetails(tableModel)

    companion object {
        fun newInstance(): CookTablesFragment = CookTablesFragment()
    }
}
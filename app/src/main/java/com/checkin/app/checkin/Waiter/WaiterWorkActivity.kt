package com.checkin.app.checkin.Waiter

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.zsmb.materialdrawerkt.builders.DrawerBuilderKt
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Shop.ShopModel.PAYMENT_MODE
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.pass
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableFragment
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableFragment.Companion.newInstance
import com.checkin.app.checkin.Waiter.Fragment.WaiterTableFragment.WaiterTableInteraction
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel
import com.checkin.app.checkin.Waiter.WaiterEndDrawerTableAdapter.OnTableClickListener
import com.checkin.app.checkin.accounts.ACCOUNT_TYPE
import com.checkin.app.checkin.accounts.BaseAccountActivity
import com.checkin.app.checkin.data.notifications.MESSAGE_TYPE
import com.checkin.app.checkin.data.notifications.MessageObjectModel
import com.checkin.app.checkin.data.notifications.MessageUtils.dismissNotification
import com.checkin.app.checkin.data.notifications.MessageUtils.parseMessage
import com.checkin.app.checkin.data.notifications.MessageUtils.registerLocalReceiver
import com.checkin.app.checkin.data.notifications.MessageUtils.unregisterLocalReceiver
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.activities.QRScannerActivity
import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.misc.views.DynamicSwipableViewPager
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE
import com.checkin.app.checkin.session.models.EventBriefModel
import com.checkin.app.checkin.session.models.RestaurantTableModel
import com.checkin.app.checkin.session.models.SessionOrderedItemModel
import com.checkin.app.checkin.session.models.TableSessionModel
import com.google.android.material.tabs.TabLayout
import com.mikepenz.materialdrawer.Drawer
import java.util.*

class WaiterWorkActivity : BaseAccountActivity(), WaiterTableInteraction, OnTableClickListener {
    @BindView(R.id.toolbar_waiter)
    internal lateinit var toolbarWaiter: Toolbar
    @BindView(R.id.tabs_waiter)
    internal lateinit var tabLayout: TabLayout
    @BindView(R.id.pager_waiter)
    internal lateinit var pagerTables: DynamicSwipableViewPager

    private val rvAssignedTables by lazy { endDrawerView.findViewById<RecyclerView>(R.id.rv_waiter_drawer_assigned_tables) }
    private val rvUnassignedTables by lazy { endDrawerView.findViewById<RecyclerView>(R.id.rv_waiter_drawer_unassigned_tables) }
    private val rvInactiveTables by lazy { endDrawerView.findViewById<RecyclerView>(R.id.rv_waiter_drawer_inactive_tables) }

    private val mViewModel: WaiterWorkViewModel by viewModels()
    private val mFragmentAdapter: WaiterTablePagerAdapter by lazy { WaiterTablePagerAdapter(supportFragmentManager) }
    private lateinit var endDrawer: Drawer
    private val endDrawerView: View by lazy { View.inflate(this, R.layout.incl_waiter_drawer_tables, null) }

    override val toolbarView: Toolbar?
        get() = toolbarWaiter

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = parseMessage(intent) ?: return
            val shop = message.shopDetail
            if (shop != null && shop.pk != mViewModel.shopPk) return
            val eventModel: EventBriefModel
            val user: BriefModel
            val orderedItemModel: SessionOrderedItemModel?
            val sessionPk: Long
            when (message.type) {
                MESSAGE_TYPE.WAITER_SESSION_NEW -> {
                    val tableName = message.rawData!!.sessionTableName
                    eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief)
                    val sessionModel = TableSessionModel(message.`object`!!.pk, null, eventModel)
                    val tableModel = RestaurantTableModel(RestaurantTableModel.NO_QR_ID, tableName, sessionModel)
                    if (message.actor!!.type == MessageObjectModel.MESSAGE_OBJECT_TYPE.RESTAURANT_MEMBER) {
                        user = message.actor.briefModel
                        sessionModel.host = user
                    }
                    addTable(tableModel)
                    updateTableStatus(sessionModel.pk)
                }
                MESSAGE_TYPE.WAITER_SESSION_NEW_ORDER -> {
                    orderedItemModel = message.rawData!!.sessionOrderedItem
                    sessionPk = message.target!!.pk
                    eventModel = EventBriefModel.getFromManagerEventModel(message.rawData.sessionEventBrief)
                    addNewOrder(sessionPk, orderedItemModel, eventModel)
                    updateTableStatus(sessionPk)
                }
                MESSAGE_TYPE.WAITER_SESSION_COLLECT_CASH -> {
                    sessionPk = message.`object`!!.pk
                    collectCash(sessionPk, message.rawData!!.sessionBillTotal!!, message.rawData.sessionBillPaymentMode)
                    updateTableStatus(sessionPk)
                }
                MESSAGE_TYPE.WAITER_SESSION_EVENT_SERVICE -> {
                    sessionPk = message.target!!.pk
                    eventModel = EventBriefModel.getFromManagerEventModel(message.rawData!!.sessionEventBrief)
                    addNewServiceEvent(sessionPk, eventModel, message.rawData.sessionEventBrief!!.status)
                    updateTableStatus(sessionPk)
                }
                MESSAGE_TYPE.WAITER_SESSION_HOST_ASSIGNED -> {
                    sessionPk = message.target!!.pk
                    user = message.`object`!!.briefModel
                    updateTableHost(sessionPk, user)
                }
                MESSAGE_TYPE.WAITER_SESSION_MEMBER_CHANGE -> {
                    sessionPk = message.`object`!!.pk
                    val customerCount = message.rawData!!.sessionCustomerCount!!
                    updateTableCustomerCount(sessionPk, customerCount)
                }
                MESSAGE_TYPE.WAITER_SESSION_UPDATE_ORDER, MESSAGE_TYPE.WAITER_SESSION_EVENT_UPDATE -> {
                    sessionPk = message.target!!.pk
                    val eventId = message.`object`!!.pk
                    updateEventStatus(sessionPk, eventId, message.rawData!!.sessionEventStatus)
                    updateTableStatus(sessionPk)
                }
                MESSAGE_TYPE.WAITER_SESSION_END -> {
                    sessionPk = message.`object`!!.pk
                    endSession(sessionPk)
                }
                MESSAGE_TYPE.WAITER_SESSION_SWITCH_TABLE -> mViewModel.fetchWaiterServedTables()
            }
        }
    }
    private var sessionPk: Long = 0

    override val accountTypes: Array<ACCOUNT_TYPE> = arrayOf(ACCOUNT_TYPE.RESTAURANT_WAITER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiter_work)
        ButterKnife.bind(this)
        setupUi()
        initRefreshScreen(R.id.sr_waiter_work)

        setupShopAssignedTables()
        setupTableFragments()
        fetchData()
        setupDrawer()
        if (intent.action != null && intent.action == ACTION_NEW_TABLE) {
            mViewModel.processQrPk(intent.getLongExtra(KEY_SESSION_QR_ID, 0L))
        }
    }

    private fun setupDrawer() {
        mViewModel.waiterStats.observe(this, Observer { resource ->
            if (resource.status === Resource.Status.SUCCESS && resource.data != null) onUpdateDrawer()
        })
    }

    private fun fetchData() {
        val shopPk = intent.getLongExtra(KEY_SHOP_PK, 0L)
        mViewModel.fetchShopTables(shopPk)
        mViewModel.fetchWaiterServedTables()
        mViewModel.fetchWaiterStats()
    }

    private fun setupTableFragments() {
        pagerTables.adapter = mFragmentAdapter
        tabLayout.setupWithViewPager(pagerTables)
        sessionPk = intent.getLongExtra(KEY_SESSION_PK, 0L)
        mViewModel.waiterTables.observe(this, Observer { listResource ->
            if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) {
                stopRefreshing()
                mFragmentAdapter.setTables(tabLayout, listResource.data, this)
                val index = mFragmentAdapter.getTableIndex(sessionPk)
                if (index > 0) pagerTables.setCurrentItem(index, true)
            } else if (listResource.status === Resource.Status.LOADING) {
                startRefreshing()
            } else {
                stopRefreshing()
            }
        })
        mViewModel.qrResult.observe(this, Observer { qrResource ->
            if (qrResource.status === Resource.Status.SUCCESS && qrResource.data != null) {
                addWaiterTable(qrResource.data.sessionPk, qrResource.data.table)
                mViewModel.fetchShopTables(mViewModel.shopPk)
            } else if (qrResource.status !== Resource.Status.LOADING) {
                Utils.toast(this, qrResource.message)
            }
        })
        pagerTables.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val data = mFragmentAdapter.getTable(position) ?: return
                data.resetEventCount()
                dismissNotification(this@WaiterWorkActivity, MessageObjectModel.MESSAGE_OBJECT_TYPE.SESSION, data.pk)
                mFragmentAdapter.updateTableStatus(tabLayout, position)
            }
        })
    }

    private fun addWaiterTable(sessionPk: Long, table: String?) {
        this.sessionPk = sessionPk
        val tableModel = WaiterTableModel(sessionPk, table)
        mViewModel.addWaiterTable(tableModel)
    }

    private fun setupShopAssignedTables() {
        val assignedTableAdapter = WaiterEndDrawerTableAdapter(null)
        val unassignedTableAdapter = WaiterEndDrawerTableAdapter(this)
        val inactiveTableAdapter = WaiterEndDrawerTableAdapter(this)
        rvAssignedTables?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvUnassignedTables?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvInactiveTables?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvAssignedTables?.adapter = assignedTableAdapter
        rvUnassignedTables?.adapter = unassignedTableAdapter
        rvInactiveTables?.adapter = inactiveTableAdapter
        mViewModel.shopAssignedTables.observe(this, Observer { listResource ->
            if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) assignedTableAdapter.setData(listResource.data)
        })
        mViewModel.shopUnassignedTables.observe(this, Observer { listResource ->
            if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) unassignedTableAdapter.setData(listResource.data)
        })
        mViewModel.shopInactiveTables.observe(this, Observer { listResource ->
            if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) inactiveTableAdapter.setData(listResource.data)
        })
    }

    override fun buildDrawer() {
        super.buildDrawer()
        endDrawer = drawer {
            gravity = Gravity.END
            customView = endDrawerView
            toolbar = toolbarWaiter
            primaryDrawer = mainDrawer
            actionBarDrawerToggleEnabled = false
        }
    }

    override fun onUpdateDrawer() {
        endDrawer.slider.removeView(endDrawerView)
        super.onUpdateDrawer()
    }

    override fun updateScreen() {
        accountViewModel.updateResults()
        mViewModel.updateResults()
        mViewModel.fetchWaiterServedTables()
    }

    // region UI-Update
    private fun addTable(tableModel: RestaurantTableModel) {
        mViewModel.addRestaurantTable(tableModel)
    }

    private fun updateTableStatus(sessionPk: Long) {
        val index = mFragmentAdapter.getTableIndex(sessionPk)
        val tableModel = mFragmentAdapter.getTable(index) ?: return
        if (index == pagerTables.currentItem) {
            tableModel.resetEventCount()
        } else {
            tableModel.increaseEventCount()
        }
        mFragmentAdapter.updateTableStatus(tabLayout, index)
    }

    private fun addNewOrder(sessionPk: Long, orderedItemModel: SessionOrderedItemModel?, eventBriefModel: EventBriefModel) {
        val viewModel = mFragmentAdapter.getTableViewModel(sessionPk)
        if (viewModel != null) {
            val eventModel = WaiterEventModel.fromEventBriefModel(eventBriefModel)
            eventModel.status = orderedItemModel!!.status
            eventModel.orderedItem = orderedItemModel
            viewModel.addNewEvent(eventModel)
        }
    }

    private fun collectCash(sessionPk: Long, sessionBillTotal: Double, sessionBillPaymentMode: PAYMENT_MODE?) {
        val viewModel = mFragmentAdapter.getTableViewModel(sessionPk)
        viewModel?.initiateCollectCash(sessionBillTotal, sessionBillPaymentMode)
    }

    private fun updateTableHost(sessionPk: Long, host: BriefModel) {
        mViewModel.updateShopTable(sessionPk, host)
    }

    private fun updateEventStatus(sessionPk: Long, eventId: Long, sessionEventStatus: CHAT_STATUS_TYPE?) {
        val viewModel = mFragmentAdapter.getTableViewModel(sessionPk)
        viewModel?.updateOrderItemStatus(eventId, sessionEventStatus)
    }

    private fun addNewServiceEvent(sessionPk: Long, eventModel: EventBriefModel, status: CHAT_STATUS_TYPE) {
        val viewModel = mFragmentAdapter.getTableViewModel(sessionPk)
        if (viewModel != null) {
            val waiterEventModel = WaiterEventModel.fromEventBriefModel(eventModel)
            waiterEventModel.status = status
            viewModel.addNewEvent(waiterEventModel)
        }
    }

    private fun updateTableCustomerCount(sessionPk: Long, customerCount: Int) {
        val viewModel = mFragmentAdapter.getTableViewModel(sessionPk)
        viewModel?.updateMemberCount(customerCount)
    }

    override fun endSession(sessionPk: Long) {
        mViewModel.markSessionEnd(sessionPk)
    }

    // endregion
    @OnClick(R.id.im_waiter_scanner)
    fun onClickScanner() {
        val intent = Intent(applicationContext, QRScannerActivity::class.java)
        startActivityForResult(intent, REQUEST_QR_SCANNER)
    }

    @OnClick(R.id.im_toolbar_waiter_table)
    fun onClickTable() {
        endDrawer.openDrawer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_QR_SCANNER && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(QRScannerActivity.KEY_QR_RESULT)
            mViewModel.processQr(qrData)
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        val types = arrayOf(
                MESSAGE_TYPE.WAITER_SESSION_NEW, MESSAGE_TYPE.WAITER_SESSION_NEW_ORDER, MESSAGE_TYPE.WAITER_SESSION_COLLECT_CASH,
                MESSAGE_TYPE.WAITER_SESSION_EVENT_SERVICE, MESSAGE_TYPE.WAITER_SESSION_HOST_ASSIGNED, MESSAGE_TYPE.WAITER_SESSION_MEMBER_CHANGE,
                MESSAGE_TYPE.WAITER_SESSION_UPDATE_ORDER, MESSAGE_TYPE.WAITER_SESSION_END, MESSAGE_TYPE.WAITER_SESSION_EVENT_UPDATE,
                MESSAGE_TYPE.WAITER_SESSION_SWITCH_TABLE
        )
        accountViewModel.updateResults()
        mViewModel.updateResults()
        registerLocalReceiver(this, mReceiver, *types)
    }

    override fun onPause() {
        super.onPause()
        unregisterLocalReceiver(this, mReceiver)
    }

    override fun setupDrawerItems(builder: DrawerBuilderKt) = builder.run {
        selectedItem = -1
        val data = mViewModel.waiterStats.value?.data
        primaryItem {
            name = getString(R.string.menu_waiter_orders_taken, data?.formatOrdersTaken() ?: "0")
            enabled = true
            icon = R.drawable.ic_orders_taken
        }
        primaryItem {
            icon = R.drawable.ic_orders_list
            name = getString(R.string.menu_waiter_total_tip, data?.tipOfDay.let {
                Utils.formatCurrencyAmount(this@WaiterWorkActivity, it ?: 0.0)
            })
            enabled = true
        }
        pass
    }

    override fun onTableClick(restaurantTableModel: RestaurantTableModel) {
        endDrawer.closeDrawer()
        newWaiterSessionDialog(restaurantTableModel.qrPk, restaurantTableModel.table)
    }

    private fun newWaiterSessionDialog(qrPk: Long, tableName: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
                .setTitle(tableName)
                .setMessage("Do you want to be host of this table?")
                .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> mViewModel.processQrPk(qrPk) }.setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                .show()
    }

    private class WaiterTablePagerAdapter internal constructor(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_SET_USER_VISIBLE_HINT) {
        private val mFragmentList: MutableList<WaiterTableFragment> = ArrayList()
        private val mTableList: MutableList<WaiterTableModel> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTableList[position].table
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItemPosition(`object`: Any): Int {
            val fragment = `object` as WaiterTableFragment
            val index = mFragmentList.indexOf(fragment)
            return if (index > -1) index else PagerAdapter.POSITION_NONE
        }

        fun resetTables() {
            mTableList.clear()
            mFragmentList.clear()
            notifyDataSetChanged()
        }

        fun setTables(tabLayout: TabLayout?, tableModels: List<WaiterTableModel>?, listener: WaiterTableInteraction?) {
            resetTables()
            for (tableModel in tableModels!!) {
                mTableList.add(tableModel)
                mFragmentList.add(newInstance(tableModel.pk))
            }
            updateTabUi(tabLayout)
        }

        fun updateTabUi(tabLayout: TabLayout?) {
            notifyDataSetChanged()
            var i = 0
            val length = mTableList.size
            while (i < length) {
                setTabCustomView(tabLayout, i, mTableList[i])
                i++
            }
        }

        fun addTable(tabLayout: TabLayout?, tableModel: WaiterTableModel, listener: WaiterTableInteraction?) {
            mTableList.add(tableModel)
            mFragmentList.add(newInstance(tableModel.pk))
            updateTabUi(tabLayout)
        }

        fun getTableViewModel(tablePk: Long): WaiterTableViewModel? {
            var pos = -1
            var i = 0
            val count = mTableList.size
            while (i < count) {
                if (mTableList[i].pk == tablePk) {
                    pos = i
                    break
                }
                i++
            }
            if (pos > -1) {
                val fragment = mFragmentList[pos]
                return fragment.viewModel
            }
            return null
        }

        private fun setTabCustomView(tabLayout: TabLayout?, index: Int, tableModel: WaiterTableModel) {
            val tab = tabLayout!!.getTabAt(index)
            if (tab != null) {
                val view = LayoutInflater.from(tabLayout.context).inflate(R.layout.view_tab_badge, null, false)
                updateTabView(view, tableModel.table, tableModel.formatEventCount())
                tab.customView = view
            }
        }

        fun getTableIndex(tableId: Long): Int {
            var index = -1
            if (tableId == 0L) return index
            var i = 0
            val length = mTableList.size
            while (i < length) {
                if (mTableList[i].pk == tableId) {
                    index = i
                    break
                }
                i++
            }
            return index
        }

        fun getTable(index: Int): WaiterTableModel? {
            return if (index < 0) null else mTableList[index]
        }

        fun updateTableStatus(tabLayout: TabLayout?, index: Int) {
            val tab = tabLayout!!.getTabAt(index)
            val data = mTableList[index]
            if (tab != null && tab.customView != null) {
                if (data.eventCount > 0) {
                    val fragment = mFragmentList[index]
                    mFragmentList.removeAt(index)
                    mTableList.removeAt(index)
                    mTableList.add(0, data)
                    mFragmentList.add(0, fragment)
                    updateTabUi(tabLayout)
                } else {
                    updateTabView(tab.customView, data.table, null)
                }
            }
        }

        private fun updateTabView(view: View?, title: String, badge: String?) {
            val tvTitle = view!!.findViewById<TextView>(R.id.tv_tab_title)
            val tvBadge = view.findViewById<TextView>(R.id.tv_tab_badge)
            tvTitle.text = title
            if (badge != null) {
                tvBadge.text = badge
                tvBadge.visibility = View.VISIBLE
            } else {
                tvBadge.visibility = View.GONE
            }
        }
    }

    companion object {
        const val KEY_SHOP_PK = "waiter.shop_pk"
        const val KEY_SESSION_PK = "waiter.session_pk"
        const val ACTION_NEW_TABLE = "waiter.new_table"
        const val KEY_SESSION_QR_ID = "waiter.session_qr_id"
        private const val REQUEST_QR_SCANNER = 121
    }
}
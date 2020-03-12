package com.checkin.app.checkin.misc.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.BillHolder

abstract class BaseOrderDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_user_scheduled_detail_orders_common

    @BindView(R.id.epoxy_rv_user_scheduled_orders)
    internal lateinit var epoxyRvOrders: EpoxyRecyclerView
    @BindView(R.id.tv_user_scheduled_session_total)
    internal lateinit var tvTotal: TextView
    @BindView(R.id.tv_user_scheduled_remarks)
    internal lateinit var tvRemarks: TextView

    lateinit var billHolder: BillHolder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        billHolder = BillHolder(view)
    }
}
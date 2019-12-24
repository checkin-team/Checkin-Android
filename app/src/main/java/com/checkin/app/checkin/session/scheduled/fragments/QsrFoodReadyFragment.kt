package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.misc.BillHolder
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.misc.holders.textModelHolder
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel

class QsrFoodReadyFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_qsr_order_ready

    @BindView(R.id.tv_qsr_ready_restaurant)
    internal lateinit var tvRestaurantName: TextView
    @BindView(R.id.tv_qsr_ready_amount)
    internal lateinit var tvAmount: TextView
    @BindView(R.id.tv_qsr_ready_order_id)
    internal lateinit var tvOrderId: TextView
    @BindView(R.id.tv_qsr_ready_restaurant_location)
    internal lateinit var tvLocation: TextView
    @BindView(R.id.tv_qsr_ready_total)
    internal lateinit var tvTotal: TextView
    @BindView(R.id.epoxy_rv_qsr_ready_orders_summary)
    internal lateinit var epoxyRvOrders: EpoxyRecyclerView

    val viewModel: ScheduledSessionDetailViewModel by viewModels()
    lateinit var billHolder: BillHolder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionId = arguments?.getLong(KEY_SESSION_ID, 0L) ?: 0L
        viewModel.fetchSessionData(sessionId)

        billHolder = BillHolder(view)
        viewModel.sessionData.observe(this, Observer {
            if (it?.status == Resource.Status.SUCCESS && it.data != null) setupData(it.data)
            else if (it.status == Resource.Status.LOADING) billHolder.showLoading()
        })

        epoxyRvOrders.withModels {
            viewModel.ordersData.value?.data?.takeIf { it.isNotEmpty() }?.forEach {
                textModelHolder {
                    id("order", it.longPk)
                    text("${it.item.name} (${it.quantity})")
                }
            }
        }
        viewModel.ordersData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) epoxyRvOrders.requestModelBuild()
        })
    }

    private fun setupData(data: CustomerScheduledSessionDetailModel) {
        tvRestaurantName.text = data.restaurant.name
        tvAmount.text = Utils.formatCurrencyAmount(requireContext(), data.bill.total)
        tvTotal.text = tvAmount.text
        tvOrderId.text = data.hashId
        tvLocation.text = data.restaurant.formatAddress
        billHolder.bind(data.bill)
    }

    companion object {
        const val KEY_SESSION_ID = "qsr.session_id"

        fun newInstance(sessionId: Long) = QsrFoodReadyFragment().apply {
            arguments = bundleOf(KEY_SESSION_ID to sessionId)
        }
    }
}
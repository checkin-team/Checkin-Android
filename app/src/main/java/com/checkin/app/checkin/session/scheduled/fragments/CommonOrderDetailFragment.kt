package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.menu.holders.invoiceOrderModelHolder
import com.checkin.app.checkin.misc.BillHolder
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel

class CommonOrderDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_user_scheduled_detail_orders_common

    @BindView(R.id.epoxy_rv_user_scheduled_orders)
    internal lateinit var epoxyRvOrders: EpoxyRecyclerView
    @BindView(R.id.tv_user_scheduled_session_total)
    internal lateinit var tvTotal: TextView
    @BindView(R.id.tv_user_scheduled_remarks)
    internal lateinit var tvRemarks: TextView

    lateinit var billHolder: BillHolder
    val viewModel: ScheduledSessionDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        billHolder = BillHolder(view)

        epoxyRvOrders.withModels {
            viewModel.ordersData.value?.data?.takeIf { it.isNotEmpty() }?.forEach {
                invoiceOrderModelHolder {
                    id(it.pk)
                    orderData(it)
                }
            }
        }
        viewModel.ordersData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) epoxyRvOrders.requestModelBuild()
        })
        viewModel.sessionData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) {
                setupData(it.data)
            } else if (it.status == Resource.Status.LOADING) {
                billHolder.showLoading()
            }
        })
    }

    private fun setupData(data: CustomerScheduledSessionDetailModel) {
        billHolder.bind(data.bill)
        tvRemarks.text = data.scheduled.remarks ?: ""
    }
}
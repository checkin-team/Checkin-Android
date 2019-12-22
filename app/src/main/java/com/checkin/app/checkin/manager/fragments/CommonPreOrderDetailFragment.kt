package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.Data.Resource
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Utility.isNotEmpty
import com.checkin.app.checkin.manager.models.ShopScheduledSessionDetailModel
import com.checkin.app.checkin.manager.viewmodels.ManagerLivePreOrdersViewModel
import com.checkin.app.checkin.menu.holders.invoiceOrderWithCustomizationModelHolder
import com.checkin.app.checkin.misc.BillHolder
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CommonPreOrderDetailFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_scheduled_common

    @BindView(R.id.epoxy_rv_manager_scheduled_orders)
    internal lateinit var epoxyRvOrders: EpoxyRecyclerView
    @BindView(R.id.tv_manager_scheduled_session_total)
    internal lateinit var tvTotal: TextView
    @BindView(R.id.tv_manager_scheduled_info)
    internal lateinit var tvInfo: TextView
    @BindView(R.id.tv_manager_scheduled_remarks)
    internal lateinit var tvRemarks: TextView

    lateinit var billHolder: BillHolder
    val viewModel: ManagerLivePreOrdersViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        billHolder = BillHolder(view)

        epoxyRvOrders.withModels {
            viewModel.sessionData.value?.data?.orderedItems.takeIf { it.isNotEmpty() }?.forEach {
                invoiceOrderWithCustomizationModelHolder {
                    id(it.pk)
                    orderData(it)
                }
            }
        }
        viewModel.sessionData.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) {
                epoxyRvOrders.requestModelBuild()
                setupData(it.data)
            } else if (it.status == Resource.Status.LOADING) {
                billHolder.showLoading()
            }
        })
    }

    private fun setupData(data: ShopScheduledSessionDetailModel) {
        billHolder.bind(data.bill)
        tvRemarks.text = data.scheduled.remarks ?: ""
        tvInfo.text = "${data.scheduled.formatPlannedDate} | ${data.scheduled.formatPlannedTime} | ${data.scheduled.formatGuestCount}"
        tvTotal.text = Utils.formatCurrencyAmount(requireContext(), data.bill.total)
    }
}

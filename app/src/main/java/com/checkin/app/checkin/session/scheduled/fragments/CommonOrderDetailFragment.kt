package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.holders.invoiceOrderModelHolder
import com.checkin.app.checkin.misc.fragments.BaseOrderDetailFragment
import com.checkin.app.checkin.session.models.CustomerScheduledSessionDetailModel
import com.checkin.app.checkin.session.scheduled.viewmodels.ScheduledSessionDetailViewModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty

class CommonOrderDetailFragment : BaseOrderDetailFragment() {


    val viewModel: ScheduledSessionDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epoxyRvOrders.withModels {
            viewModel.ordersData.value?.data?.takeIf { it.isNotEmpty() }?.forEach {
                invoiceOrderModelHolder {
                    id(it.pk)
                    orderData(it)
                }
            }
        }
        viewModel.ordersData.observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS && it.data != null) epoxyRvOrders.requestModelBuild()
        })
        viewModel.sessionData.observe(viewLifecycleOwner, Observer {
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
        tvTotal.text = Utils.formatCurrencyAmount(requireContext(), data.bill.total)
    }
}
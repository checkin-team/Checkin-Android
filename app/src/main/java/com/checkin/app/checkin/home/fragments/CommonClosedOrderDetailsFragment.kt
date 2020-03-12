package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.invoiceClosedOrderModelHolder
import com.checkin.app.checkin.home.model.ClosedSessionDetailsModel
import com.checkin.app.checkin.home.viewmodels.ClosedSessionViewModel
import com.checkin.app.checkin.misc.fragments.BaseOrderDetailFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty


class CommonClosedOrderDetailsFragment : BaseOrderDetailFragment() {


    val viewModel: ClosedSessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epoxyRvOrders.withModels {
            viewModel.ordersData.value?.data.takeIf { it.isNotEmpty() }?.forEach {
                invoiceClosedOrderModelHolder {
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

    private fun setupData(data: ClosedSessionDetailsModel) {
        billHolder.bind(data.bill)
        tvTotal.text = Utils.formatCurrencyAmount(requireContext(), data.bill.total)
    }
}
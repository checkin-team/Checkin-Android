package com.checkin.app.checkin.home.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.epoxy.invoiceClosedOrderModelHolder
import com.checkin.app.checkin.home.model.PastSessionDetailsModel
import com.checkin.app.checkin.home.viewmodels.PaymentDetailsViewModel
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.isNotEmpty


class ClosedOrderDetailsFragment : BaseOrderDetailFragment() {


    val viewModel: PaymentDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epoxyRvOrders.withModels {
            viewModel.ordersData.value?.data.takeIf { it.isNotEmpty() }?.forEach {
                invoiceClosedOrderModelHolder {
                    id(it.pk)
                    orderData(it)
                    Log.d("Bruh", "okay cool")
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

    private fun setupData(data: PastSessionDetailsModel) {
        billHolder.bind(data.bill)
        tvTotal.text = Utils.formatCurrencyAmount(requireContext(), data.bill.total)
    }
}
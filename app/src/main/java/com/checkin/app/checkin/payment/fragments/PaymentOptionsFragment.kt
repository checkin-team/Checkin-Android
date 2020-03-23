package com.checkin.app.checkin.payment.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.controllers.PaymentOptionsController
import com.checkin.app.checkin.payment.listeners.PaymentOptionSelectListener
import com.checkin.app.checkin.payment.models.PAYMENT_TYPE
import com.checkin.app.checkin.payment.models.PaymentOptionModel
import com.checkin.app.checkin.utility.toast

class PaymentOptionsFragment : BaseFragment(), PaymentOptionSelectListener {
    override val rootLayout: Int = R.layout.fragment_payment_options

    @BindView(R.id.epoxy_rv_payment_options)
    lateinit var epoxyRvOptions: EpoxyRecyclerView

    private val viewModel: PaymentViewModel by activityViewModels()
    private val controller by lazy { PaymentOptionsController(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        epoxyRvOptions.setHasFixedSize(false)
        epoxyRvOptions.setControllerAndBuildModels(controller)

        setupObserver()
        viewModel.fetchUPIOptions()
        viewModel.fetchCardOptions()
    }

    private fun setupObserver() {
        viewModel.upiOptions.observe(this, Observer {
            it?.let { resource ->
                if (resource.status == Resource.Status.SUCCESS && resource.data != null)
                    controller.upiOptions = resource.data
            }
        })
        viewModel.netBankingOptions.observe(this, Observer {
            it?.let { resource ->
                if (resource.status == Resource.Status.SUCCESS && resource.data != null)
                    controller.netBankingOptions = resource.data.slice(0 until resource.data.size.coerceAtMost(5))
            }
        })
        viewModel.cardOptions.observe(this, Observer {
            it?.let { resource ->
                if (resource.status == Resource.Status.SUCCESS && resource.data != null)
                    controller.cardOptions = resource.data
            }
        })
        viewModel.recentlyUsedOptions.observe(this, Observer {
            controller.lastUsedOptions = it?.run { subList(0, size.coerceAtMost(5)) }
        })
    }

    override fun onAddPaymentOption(pmtType: PAYMENT_TYPE) {
        val action = when (pmtType) {
            PAYMENT_TYPE.UPI -> PaymentOptionsFragmentDirections.actionPaymentOptionsFragmentToPaymentUpiFragment()
            PAYMENT_TYPE.CARD -> PaymentOptionsFragmentDirections.actionPaymentOptionsFragmentToPaymentCardFragment()
            else -> {
                toast("Not yet added")
                return
            }
        }
        kotlin.runCatching {
            findNavController().navigate(action)
        }
    }

    override fun onPayPaymentOption(paymentOption: PaymentOptionModel) {
        viewModel.payUsing(paymentOption, saveOption = true)
    }
}
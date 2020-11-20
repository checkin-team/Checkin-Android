package com.checkin.app.checkin.manager.fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.viewmodels.ManagerSessionViewModel
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.utility.Utils

class ManagerSessionPaymentOptionsBottomSheetFragment : BaseBottomSheetFragment() {
    override val rootLayout: Int = R.layout.fragment_manager_session_payment_options

    @BindView(R.id.tv_manager_session_payment_amount)
    internal lateinit var tvManagerSessionAmount: TextView

    private val viewModel: ManagerSessionViewModel by activityViewModels()

    private val isPromoApplied by lazy {
        viewModel.isPromoApplied
    }

    private val isRequestedCheckout by lazy {
        viewModel.isRequestedCheckout
    }

    @OnClick(R.id.card_manager_session_payment_bhim, R.id.card_manager_session_payment_card, R.id.card_manager_session_payment_cash)
    fun submitButton(v: View) {
        when (v.id) {
            R.id.card_manager_session_payment_bhim -> viewModel.setPaymentMode(ManagerSessionViewModel.PAYMENT_MODE.UPI)
            R.id.card_manager_session_payment_card -> viewModel.setPaymentMode(ManagerSessionViewModel.PAYMENT_MODE.CARD)
            R.id.card_manager_session_payment_cash -> viewModel.setPaymentMode(ManagerSessionViewModel.PAYMENT_MODE.CASH)
        }

        val builder = AlertDialog.Builder(requireContext()).setTitle("Are you sure you want to close session?")
                .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        if (!isPromoApplied || isRequestedCheckout) builder.setPositiveButton("Close session") { _: DialogInterface, _: Int ->
            viewModel.putSessionCheckout()
            dialog?.dismiss()
            dismiss()
        } else builder.setPositiveButton("Notify waiter") { dialog: DialogInterface?, _: Int ->
            viewModel.requestSessionCheckout()
            dialog?.dismiss()
            dismiss()
        }
        if (!isPromoApplied && !isRequestedCheckout)
            builder.setNeutralButton("Notify waiter") { dialog: DialogInterface?, i: Int ->
                viewModel.requestSessionCheckout()
                dialog?.dismiss()
                dismiss()

            }
        builder.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sessionInvoice.observe(viewLifecycleOwner, Observer { resource ->
            if (resource == null) return@Observer
            if (resource.status === Resource.Status.SUCCESS && resource.data != null) {
                val total = resource.data.bill.total
                tvManagerSessionAmount.text = Utils.formatCurrencyAmount(context, total)
            }
        })
    }

    companion object {
        fun newInstance() = ManagerSessionPaymentOptionsBottomSheetFragment()

    }
}

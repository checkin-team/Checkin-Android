package com.checkin.app.checkin.payment.fragments

import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel

class PaymentOptionAddUpiFragment : BaseBottomSheetFragment() {
    override val rootLayout = R.layout.fragment_payment_option_add_upi

    @BindView(R.id.et_payment_option_upi_vpa)
    internal lateinit var etVpa: EditText
    @BindView(R.id.cb_payment_option_save_upi)
    internal lateinit var cbSaveUpi: CheckBox

    private val viewModel: PaymentViewModel by activityViewModels()

    @OnClick(R.id.btn_payment_option_pay_upi)
    fun onPayClick() {
        val vpa = etVpa.text.toString()
        if (vpa.isBlank() || vpa.split('@').size != 2)
            etVpa.error = "Input correct UPI ID"
        viewModel.payUsing(UPICollectPaymentOptionModel(vpa), saveOption = cbSaveUpi.isChecked)
    }
}

package com.checkin.app.checkin.payment.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.utility.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PaymentOptionAddUpiFragment : BaseBottomSheetFragment() {
    override val rootLayout = R.layout.fragment_payment_option_add_upi

    @BindView(R.id.et_payment_option_upi_vpa)
    internal lateinit var etVpa: EditText
    @BindView(R.id.cb_payment_option_save_upi)
    internal lateinit var cbSaveUpi: CheckBox
    @BindView(R.id.btn_payment_option_pay_upi)
    internal lateinit var btnPay: Button

    private val viewModel: PaymentViewModel by activityViewModels()
    private var validationJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnPay.isActivated = false
        etVpa.addTextChangedListener {
            validationJob?.also {
                it.cancel("Enqueued new job")
                validationJob = null
            }
            validationJob = coroutineLifecycleScope.launch {
                var errMsg = if (it != null && it.isNotBlank() && it.split('@').size == 2) null else "Input correct UPI ID"
                if (errMsg == null)
                    errMsg = if (!viewModel.isValidVpa(it.toString())) "Entered UPI is invalid!" else null
                btnPay.addError(view = etVpa, msg = errMsg)
                btnPay.isActivated = btnPay.isValidForm
            }
        }
        etVpa.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnPay.performClick()
                handled = true
            }
            return@setOnEditorActionListener handled
        }
    }

    @OnClick(R.id.btn_payment_option_pay_upi)
    fun onPayClick() {
        if (validationJob?.isActive == true)
            toast("Validating input...")
        if (!validateForm(btnPay)) return
        val vpa = etVpa.text.toString()
        viewModel.payUsing(UPICollectPaymentOptionModel(vpa), saveOption = cbSaveUpi.isChecked)
        dismiss()
    }
}

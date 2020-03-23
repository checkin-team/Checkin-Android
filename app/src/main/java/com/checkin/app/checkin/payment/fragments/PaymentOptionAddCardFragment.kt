package com.checkin.app.checkin.payment.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.payment.PaymentViewModel
import com.checkin.app.checkin.payment.models.CardPaymentOptionModel
import com.checkin.app.checkin.utility.addError
import com.checkin.app.checkin.utility.isValidForm
import com.checkin.app.checkin.utility.validateField
import com.checkin.app.checkin.utility.validateForm
import com.google.android.material.textfield.TextInputEditText
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy

class PaymentOptionAddCardFragment : BaseBottomSheetFragment() {
    override val rootLayout = R.layout.fragment_payment_option_add_card

    @BindView(R.id.tet_payment_option_card_name)
    internal lateinit var tetCardName: TextInputEditText
    @BindView(R.id.tet_payment_option_card_number)
    internal lateinit var tetCardNumber: TextInputEditText
    @BindView(R.id.tet_payment_option_card_expiry)
    internal lateinit var tetExpiry: TextInputEditText
    @BindView(R.id.tet_payment_option_card_cvv)
    internal lateinit var tetCvv: TextInputEditText
    @BindView(R.id.cb_payment_option_save_card)
    internal lateinit var cbSaveCard: CheckBox
    @BindView(R.id.btn_payment_option_pay_card)
    internal lateinit var btnPay: Button

    private val maskCardNumber by lazy {
        MaskedTextChangedListener(
                field = tetCardNumber, primaryFormat = "[0000] [0000] [0000] [0000]",
                affineFormats = listOf("[0000] [000000] [00000]"),
                affinityCalculationStrategy = AffinityCalculationStrategy.CAPACITY,
                autoskip = true,
                valueListener = object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        validateCardNumber()
                        if (tetCardNumber.error == null && extractedValue.length >= 6) {
                            val cardNetwork = viewModel.getCardNetwork(extractedValue)
                            tetCardNumber.tag = cardNetwork
                            tetCardNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, cardNetwork?.drawable
                                    ?: 0, 0)
                        } else tetCardNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
        )
    }
    private val maskCardExpiry by lazy {
        MaskedTextChangedListener(
                field = tetExpiry, primaryFormat = "[00]{/}[00]", autoskip = true,
                valueListener = object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                        validateCardExpiry()
                    }
                }
        )
    }

    private val cardNumber: String
        get() = tetCardNumber.text!!.replace("\\s".toRegex(), "")

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Run validations initially to fill errors
        validateData()

        setupInputMasks()
    }

    private fun setupInputMasks() {
        // FIXME: An hack to fix keyboard not opening the first time on focusing the below edittext's
        tetCardNumber.hint = ""
        tetExpiry.hint = ""

        tetCardNumber.addTextChangedListener(maskCardNumber)
        tetCardNumber.setOnFocusChangeListener { v, hasFocus ->
            tetCardNumber.hint = if (hasFocus) maskCardNumber.placeholder() else ""
            maskCardNumber.onFocusChange(v, hasFocus)
            btnPay.validateField(tetCardNumber)
        }
        tetExpiry.addTextChangedListener(maskCardExpiry)
        tetExpiry.setOnFocusChangeListener { v, hasFocus ->
            tetExpiry.hint = if (hasFocus) maskCardExpiry.placeholder() else ""
            maskCardExpiry.onFocusChange(v, hasFocus)
            btnPay.validateField(tetExpiry)
        }
        tetCardName.addTextChangedListener { validateCardName() }
        tetCvv.addTextChangedListener { validateCvv() }
        tetCvv.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnPay.performClick()
                handled = true
            }
            return@setOnEditorActionListener handled
        }
    }

    private fun validateCardNumber() {
        val errCard = cardNumber.let {
            when {
                it.length !in 15..16 -> "Input 15/16 digit Card Number"
                !viewModel.isValidCard(it) -> "Entered Card is Invalid"
                else -> null
            }
        }
        btnPay.addError(view = tetCardNumber, msg = errCard)
        updateButtonState()
    }

    private fun validateCardExpiry() {
        val errExpiry = tetExpiry.text?.split('/')?.let {
            when {
                it.size < 2 -> "Input Expiry Year"
                it[0].toInt() !in 1..12 -> "Input correct Expiry Month"
                else -> null
            }
        }
        btnPay.addError(view = tetExpiry, msg = errExpiry)
        updateButtonState()
    }

    private fun validateCardName() {
        val errName = if (tetCardName.text?.isBlank() == true) "Input Name on Card" else null
        btnPay.addError(view = tetCardName, msg = errName)
        updateButtonState()
    }

    private fun validateCvv() {
        val errCvv = if (tetCvv.text?.length != 3) "Input the CVV written on back of card" else null
        btnPay.addError(view = tetCvv, msg = errCvv)
        updateButtonState()
    }

    private fun updateButtonState() {
        btnPay.isActivated = btnPay.isValidForm
    }

    private fun validateData() {
        validateCardNumber()
        validateCvv()
        validateCardName()
        validateCardExpiry()
        updateButtonState()
    }

    @OnClick(R.id.btn_payment_option_pay_card)
    fun onPayClick() {
        if (!validateForm(btnPay)) return
        val name = tetCardName.text.toString()
        val number = cardNumber
        val expirySplit = tetExpiry.text!!.split('/')
        val cvv = tetCvv.text.toString()
        val network = (tetCardNumber.tag as? CardPaymentOptionModel.CARD_PROVIDER)
                ?: viewModel.getCardNetwork(number)
        val data = CardPaymentOptionModel(name, number, expirySplit[0], expirySplit[1], cvv, channel = network)
        viewModel.payUsing(data, saveOption = cbSaveCard.isChecked)
        dismiss()
    }
}

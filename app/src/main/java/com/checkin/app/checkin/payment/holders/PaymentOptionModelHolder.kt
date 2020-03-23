package com.checkin.app.checkin.payment.holders

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.widget.addTextChangedListener
import butterknife.BindView
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.payment.listeners.PaymentOptionSelectListener
import com.checkin.app.checkin.payment.models.CardPaymentOptionModel
import com.checkin.app.checkin.payment.models.PaymentOptionModel
import com.checkin.app.checkin.payment.models.UPICollectPaymentOptionModel
import com.checkin.app.checkin.payment.models.UPIPushPaymentOptionModel
import com.checkin.app.checkin.utility.Utils

@EpoxyModelClass(layout = R.layout.item_payment_option)
abstract class PaymentOptionModelHolder : EpoxyModelWithHolder<PaymentOptionModelHolder.OptionHolder>() {
    @EpoxyAttribute
    var selectedId: Long = 0

    @EpoxyAttribute
    lateinit var optionData: PaymentOptionModel

    @EpoxyAttribute
    lateinit var listener: PaymentOptionSelectListener

    @EpoxyAttribute
    lateinit var selectListener: PaymentOptionInteraction

    override fun bind(holder: OptionHolder) {
        holder.bindData(optionData)
        holder.bindShowPay(selectedId == id())
    }

    inner class OptionHolder : BaseEpoxyHolder<PaymentOptionModel>() {
        @BindView(R.id.tv_payment_option_name)
        internal lateinit var tvName: TextView
        @BindView(R.id.cl_payment_option_pay)
        internal lateinit var containerPay: ViewGroup
        @BindView(R.id.rb_payment_option_select)
        internal lateinit var rbSelect: AppCompatRadioButton
        @BindView(R.id.et_payment_option_card_cvv)
        internal lateinit var etCardCvv: EditText
        @BindView(R.id.im_payment_option_icon)
        internal lateinit var imIcon: ImageView
        @BindView(R.id.cl_payment_option_select)
        internal lateinit var containerSelect: ViewGroup
        @BindView(R.id.btn_payment_option_pay)
        internal lateinit var btnPay: Button

        private lateinit var mData: PaymentOptionModel
        private var mShowPay: Boolean = false

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            rbSelect.setOnCheckedChangeListener { _, isChecked -> if (isChecked && !mShowPay) selectListener.onSelectOption(id()) }
            containerSelect.setOnClickListener { rbSelect.isChecked = true }
            etCardCvv.addTextChangedListener {
                btnPay.isActivated = it?.length == 3
            }
            etCardCvv.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnPay.performClick()
                    handled = true
                }
                return@setOnEditorActionListener handled
            }
        }

        override fun bindData(data: PaymentOptionModel) {
            mData = data

            when (data) {
                is UPIPushPaymentOptionModel -> {
                    tvName.text = data.appName
                    Utils.loadImageOrDefault(imIcon, data.iconUrl, 0)
                    etCardCvv.visibility = GONE
                    btnPay.isActivated = true
                }
                is CardPaymentOptionModel -> {
                    tvName.text = data.formatNumber
                    data.channel?.also { imIcon.setImageResource(it.drawable) }
                    etCardCvv.visibility = VISIBLE
                    btnPay.isActivated = false
                }
                is UPICollectPaymentOptionModel -> {
                    tvName.text = data.vpa
                    imIcon.setImageResource(R.drawable.ic_payment_upi)
                    etCardCvv.visibility = GONE
                    btnPay.isActivated = true
                }
            }
        }

        fun bindShowPay(showPay: Boolean) {
            rbSelect.isChecked = showPay
            mShowPay = showPay
            containerPay.visibility = if (showPay) VISIBLE else GONE
        }

        @OnClick(R.id.btn_payment_option_pay)
        fun onClickPay() {
            if (!btnPay.isActivated) {
                etCardCvv.error = "Input CVV to pay"
            }
            listener.onPayPaymentOption(mData)
        }
    }
}

interface PaymentOptionInteraction {
    fun onSelectOption(id: Long)
}
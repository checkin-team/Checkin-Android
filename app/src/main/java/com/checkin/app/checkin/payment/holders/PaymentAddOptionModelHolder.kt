package com.checkin.app.checkin.payment.holders

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.payment.listeners.PaymentOptionSelectListener
import com.checkin.app.checkin.payment.models.PAYMENT_TYPE

@EpoxyModelClass(layout = R.layout.item_payment_add_option, useLayoutOverloads = true)
abstract class PaymentAddOptionModelHolder : EpoxyModelWithHolder<PaymentAddOptionModelHolder.AddItemHolder>() {
    @EpoxyAttribute
    lateinit var content: String

    @EpoxyAttribute
    lateinit var optionType: PAYMENT_TYPE

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var listener: PaymentOptionSelectListener

    override fun bind(holder: AddItemHolder) = holder.bindData(content)

    inner class AddItemHolder : BaseEpoxyHolder<String>() {
        @BindView(R.id.container_payment_add_option)
        internal lateinit var containerAddOption: ViewGroup
        @BindView(R.id.btn_payment_add_item)
        internal lateinit var btnAddItem: Button
        @BindView(R.id.tv_payment_add_content)
        internal lateinit var tvAddContent: TextView

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            containerAddOption.setOnClickListener { listener.onAddPaymentOption(optionType) }
        }

        override fun bindData(data: String) {
            tvAddContent.text = data
        }
    }
}
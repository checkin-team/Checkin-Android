package com.checkin.app.checkin.payment.holders

import android.view.View
import android.widget.ImageView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseEpoxyHolder
import com.checkin.app.checkin.payment.listeners.PaymentOptionSelectListener
import com.checkin.app.checkin.payment.models.NetBankingPaymentOptionModel

@EpoxyModelClass(layout = R.layout.item_payment_netbanking_option)
abstract class PaymentNetBankingModelHolder : EpoxyModelWithHolder<PaymentNetBankingModelHolder.OptionHolder>() {
    @EpoxyAttribute
    lateinit var listener: PaymentOptionSelectListener

    @EpoxyAttribute
    lateinit var data: NetBankingPaymentOptionModel

    override fun createNewHolder() = OptionHolder(listener)

    override fun bind(holder: OptionHolder) = holder.bindData(data)

    class OptionHolder(val listener: PaymentOptionSelectListener) : BaseEpoxyHolder<NetBankingPaymentOptionModel>() {
        @BindView(R.id.im_payment_netbanking_option)
        internal lateinit var imOption: ImageView

        private lateinit var mData: NetBankingPaymentOptionModel

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            imOption.setOnClickListener { listener.onPayPaymentOption(mData) }
        }

        override fun bindData(data: NetBankingPaymentOptionModel) {
            mData = data
            if (mData.iconRes != 0)
                imOption.setImageResource(mData.iconRes)
        }
    }
}
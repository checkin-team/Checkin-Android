package com.checkin.app.checkin.payment.holders

import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseHolder

@EpoxyModelClass(layout = R.layout.item_payment_header)
abstract class PaymentHeaderModel : EpoxyModelWithHolder<PaymentHeaderModel.HeaderHolder>() {
    @EpoxyAttribute
    lateinit var header: String

    override fun bind(holder: HeaderHolder) {
        holder.tvHeader.text = header
    }

    inner class HeaderHolder : BaseHolder() {
        @BindView(R.id.tv_payment_header)
        lateinit var tvHeader: TextView
    }

}

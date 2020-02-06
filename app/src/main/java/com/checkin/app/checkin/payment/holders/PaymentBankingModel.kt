package com.checkin.app.checkin.payment.holders

import android.view.View
import android.widget.ImageView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseHolder

@EpoxyModelClass(layout = R.layout.item_payment_netbanking)
abstract class PaymentBankingModel : EpoxyModelWithHolder<PaymentBankingModel.ItemHolder>() {


    @EpoxyAttribute
    lateinit var listener: View.OnClickListener

    override fun bind(holder: ItemHolder) {
        holder.imSbi.setOnClickListener(listener)
        holder.imAxis.setOnClickListener(listener)
        holder.imCiti.setOnClickListener(listener)
        holder.imKotak.setOnClickListener(listener)
    }


    inner class ItemHolder : BaseHolder() {

        @BindView(R.id.im_payment_netbanking_sbi)
        internal lateinit var imSbi: ImageView

        @BindView(R.id.im_payment_netbanking_axis)
        internal lateinit var imAxis: ImageView

        @BindView(R.id.im_payment_netbanking_citi)
        internal lateinit var imCiti: ImageView

        @BindView(R.id.im_payment_netbanking_kotak)
        internal lateinit var imKotak: ImageView

    }

}
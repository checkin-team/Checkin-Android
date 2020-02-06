package com.checkin.app.checkin.payment.holders

import android.view.View
import android.widget.Button
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseHolder

@EpoxyModelClass(layout = R.layout.item_payment_add_card)
abstract class PaymentAddCardModel : EpoxyModelWithHolder<PaymentAddCardModel.AddCardHolder>() {

    @EpoxyAttribute
    internal lateinit var addListener: View.OnClickListener

    override fun bind(holder: AddCardHolder) {
        if (::addListener.isInitialized) {
            holder.btnAddCard.setOnClickListener(addListener)
        }

    }

    inner class AddCardHolder : BaseHolder() {
        @BindView(R.id.btn_payment_add_item)
        lateinit var btnAddCard: Button


    }
}
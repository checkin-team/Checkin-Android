package com.checkin.app.checkin.payment.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseHolder

@EpoxyModelClass(layout = R.layout.item_payment_add_item)
abstract class PaymentAddItemModel : EpoxyModelWithHolder<PaymentAddItemModel.AddItemHolder>() {

    @EpoxyAttribute
    var content: String = "ADD UPI ID"

    @EpoxyAttribute
    lateinit var addListener: View.OnClickListener

    override fun bind(holder: AddItemHolder) {
        holder.btnAddContent.text = content
        
        if (::addListener.isInitialized) {
            holder.btnAddItem.setOnClickListener(addListener)
        }
    }


    inner class AddItemHolder : BaseHolder() {
        @BindView(R.id.btn_payment_add_item)
        internal lateinit var btnAddItem: Button
        @BindView(R.id.tv_payment_add_content)
        internal lateinit var btnAddContent: TextView

    }

}
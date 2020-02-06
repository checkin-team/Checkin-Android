package com.checkin.app.checkin.payment.holders

import android.view.View
import android.view.View.*
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.epoxy.BaseHolder

@EpoxyModelClass(layout = R.layout.item_payment_item)
abstract class PaymentItemModel : EpoxyModelWithHolder<PaymentItemModel.ItemHolder>() {


    @EpoxyAttribute
    var showPay: Boolean = false

    @EpoxyAttribute
    var content: String = "1090-XXXXXXXX-1235"

    @EpoxyAttribute
    @DrawableRes
    var image = R.drawable.ic_payment_amex

    @EpoxyAttribute
    lateinit var payListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var selectListener: View.OnClickListener


    @EpoxyAttribute
    var type: PaymentType = PaymentType.UPI


    override fun bind(holder: ItemHolder) {
        holder.tvContent.text = content
        holder.clPay.visibility = GONE
        holder.rbSelected.isChecked = false

        if (type == PaymentType.CARD) {
            holder.btnCvv.visibility = VISIBLE
            holder.imPaymentType.setImageResource(image)
        }
        if (type == PaymentType.UPI) {
            holder.imPaymentType.setImageResource(R.drawable.ic_payment_upi)
            holder.btnCvv.visibility = INVISIBLE
        }
        if (type == PaymentType.WALLET) {
            holder.btnCvv.visibility = INVISIBLE
        }
        if (showPay) {
            holder.clPay.visibility = VISIBLE
            holder.rbSelected.isChecked = true
            if (::payListener.isInitialized) {
                holder.btnPayment.setOnClickListener(payListener)
            }
        }
        if (::selectListener.isInitialized) {
            holder.rbSelected.setOnClickListener(selectListener)
        }
    }


    inner class ItemHolder : BaseHolder() {

        @BindView(R.id.btn_payment_item_pay)
        internal lateinit var btnPayment: Button

        @BindView(R.id.btn_payment_item_cvv)
        internal lateinit var btnCvv: EditText

        @BindView(R.id.im_payment_item_type)
        internal lateinit var imPaymentType: ImageView

        @BindView(R.id.tv_payment_item_content)
        internal lateinit var tvContent: TextView

        @BindView(R.id.cl_payment_item_pay)
        internal lateinit var clPay: ConstraintLayout

        @BindView(R.id.cl_payment_item)
        internal lateinit var clItem: ConstraintLayout

        @BindView(R.id.rb_payment_item)
        internal lateinit var rbSelected: RadioButton

    }

    enum class PaymentType {
        CARD, UPI, WALLET,
    }
}
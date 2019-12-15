package com.checkin.app.checkin.Misc

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.session.model.SessionBillModel

class BillHolder(itemView: View) {
    @BindView(R.id.tv_invoice_subtotal)
    internal lateinit var tvInvoiceSubtotal: TextView
    @BindView(R.id.tv_invoice_tax)
    internal lateinit var tvInvoiceTax: TextView
    @BindView(R.id.tv_invoice_charge)
    internal lateinit var tvInvoiceCharge: TextView
    @BindView(R.id.tv_invoice_discount)
    internal lateinit var tvInvoiceDiscount: TextView
    @BindView(R.id.tv_invoice_tip)
    internal lateinit var tvInvoiceTip: TextView
    @BindView(R.id.tv_invoice_promo)
    internal lateinit var tvInvoicePromo: TextView
    @BindView(R.id.tv_invoice_promo_code)
    internal lateinit var tvInvoicePromoCode: TextView
    @BindView(R.id.container_invoice_charge)
    internal lateinit var containerInvoiceCharge: ViewGroup
    @BindView(R.id.container_invoice_tax)
    internal lateinit var containerInvoiceTax: ViewGroup
    @BindView(R.id.container_invoice_promo)
    internal lateinit var containerInvoicePromo: ViewGroup
    @BindView(R.id.container_invoice_discount)
    internal lateinit var containerInvoiceDiscount: ViewGroup

    private val mContext: Context

    init {
        ButterKnife.bind(this, itemView)
        mContext = itemView.context
        containerInvoicePromo.visibility = View.GONE
        containerInvoiceDiscount.visibility = View.GONE
    }

    fun bind(bill: SessionBillModel) {
        // Subtotal
        bill.subtotal?.let { tvInvoiceSubtotal.text = Utils.formatCurrencyAmount(mContext, it) }
        // Tax
        bill.tax.let {
            if (it != null) tvInvoiceTax.text = Utils.formatCurrencyAmount(mContext, bill.tax)
            else containerInvoiceTax.visibility = View.GONE
        }
        // Charges
        bill.charges.let {
            if (it != null && it > 0) {
                containerInvoiceCharge.visibility = View.VISIBLE
                tvInvoiceCharge.text = Utils.formatCurrencyAmount(mContext, it)
            } else containerInvoiceCharge.visibility = View.GONE
        }
        // Promo
        bill.offers.let {
            if (it != null) {
                tvInvoicePromoCode.text = bill.formatPromo()
                tvInvoicePromo.text = "- ${Utils.formatCurrencyAmount(mContext, it)}"
                containerInvoicePromo.visibility = View.VISIBLE
            } else containerInvoicePromo.visibility = View.GONE
        }
        // Discount
        bill.discount.let {
            if (it != null) {
                tvInvoiceDiscount.text = "- ${Utils.formatCurrencyAmount(mContext, it)}"
                containerInvoiceDiscount.visibility = View.VISIBLE
            } else containerInvoiceDiscount.visibility = View.GONE
        }
        // Tip
       // tvInvoiceTip.text = Utils.formatCurrencyAmount(mContext, bill.tip)
    }
}

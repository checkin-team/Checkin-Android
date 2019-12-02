package com.checkin.app.checkin.Misc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionBillModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillHolder {
    @BindView(R.id.tv_invoice_subtotal)
    TextView tvInvoiceSubtotal;
    @BindView(R.id.tv_invoice_tax)
    TextView tvInvoiceTax;
    @BindView(R.id.tv_invoice_discount)
    TextView tvInvoiceDiscount;
    @BindView(R.id.tv_invoice_tip)
    TextView tvInvoiceTip;
    @BindView(R.id.tv_invoice_promo)
    TextView tvInvoicePromo;
    @BindView(R.id.tv_brownie_cash)
    TextView tvBrownieCash;
    @BindView(R.id.tv_invoice_promo_code)
    TextView tvInvoicePromoCode;
    @BindView(R.id.container_invoice_tax)
    ViewGroup containerInvoiceTax;
    @BindView(R.id.container_invoice_promo)
    ViewGroup containerInvoicePromo;
    @BindView(R.id.container_invoice_discount)
    ViewGroup containerInvoiceDiscount;
    @BindView(R.id.container_invoice_brownie_cash)
    ViewGroup containerBrownieCash;

    private Context mContext;

    public BillHolder(View itemView) {
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        containerInvoicePromo.setVisibility(View.GONE);
        containerInvoiceDiscount.setVisibility(View.GONE);
    }

    public void bind(SessionBillModel bill) {
        // Subtotal
        if (bill.getSubtotal() != null)
            tvInvoiceSubtotal.setText(Utils.formatCurrencyAmount(mContext, bill.getSubtotal()));
        // Tax
        if (bill.getTax() != null)
            tvInvoiceTax.setText(Utils.formatCurrencyAmount(mContext, bill.getTax()));
        else
            containerInvoiceTax.setVisibility(View.GONE);
        // Promo
        if (bill.getOffers() != null) {
            tvInvoicePromoCode.setText(bill.formatPromo());
            tvInvoicePromo.setText("- "+Utils.formatCurrencyAmount(mContext, bill.getOffers()));
            containerInvoicePromo.setVisibility(View.VISIBLE);
        } else
            containerInvoicePromo.setVisibility(View.GONE);
        // Discount
        if (bill.getDiscount() != null) {
            tvInvoiceDiscount.setText("- "+Utils.formatCurrencyAmount(mContext, bill.getDiscount()));
            containerInvoiceDiscount.setVisibility(View.VISIBLE);
        } else
            containerInvoiceDiscount.setVisibility(View.GONE);
        // Tip
        tvInvoiceTip.setText(Utils.formatCurrencyAmount(mContext, bill.getTip()));

        // Brownie Cash
       /* if (bill.getBrownie() != null) {
            tvBrownieCash.setText("- "+Utils.formatCurrencyAmount(mContext, bill.getBrownie()));
            containerBrownieCash.setVisibility(View.VISIBLE);
        } else
            containerBrownieCash.setVisibility(View.GONE);*/
    }
}

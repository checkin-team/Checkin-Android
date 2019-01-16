package com.checkin.app.checkin.Session.ActiveSession;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveSessionInvoiceActivity extends AppCompatActivity {
    @BindView(R.id.rv_ordered_items) RecyclerView rv_ordered_items;
    @BindView(R.id.im_waiter) CircleImageView im_waiter;
    @BindView(R.id.tv_subtotal) TextView tv_subtotal;
    @BindView(R.id.tv_charges) TextView tv_charges;
    @BindView(R.id.tv_taxes) TextView tv_taxes;
    @BindView(R.id.tv_promo) TextView tv_promo;
    @BindView(R.id.tv_discount) TextView tv_discount;
    @BindView(R.id.tv_tip) TextView tv_tip;
    @BindView(R.id.tv_total) TextView tv_total;
    private ActiveSessionViewModel mViewModel;
    private ActiveSessionInvoiceAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_checkout);
        ButterKnife.bind(this);

        rv_ordered_items.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ActiveSessionInvoiceAdapter(null);
        rv_ordered_items.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);

//        Log.e("id===", mViewModel.getShopPk());
        mViewModel.getSessionId("3");

        mViewModel.getInvoiceData().observe(this,activeSessionInvoiceModelResource -> {
            if (activeSessionInvoiceModelResource.status == Resource.Status.SUCCESS && activeSessionInvoiceModelResource.data != null) {
                ActiveSessionInvoiceModel data = activeSessionInvoiceModelResource.data;
                mAdapter.setData(data.getOrdered_items());
                if (data.getHost() != null)
                    Utils.loadImageOrDefault(im_waiter, data.getHost().getDisplayPic(), R.drawable.ic_waiter);

                if (data.getBill().getSubtotal() != null)
                    tv_subtotal.setText(data.getBill().getSubtotal());
                if (data.getBill().getSubtotal() != null)
                    tv_charges.setText(data.getBill().getSubtotal());
                if (data.getBill().getTax() != null) tv_taxes.setText(data.getBill().getTax());
                if (data.getBill().getOffers() != null)
                    tv_promo.setText(data.getBill().getOffers());
                if (data.getBill().getDiscount() != null)
                    tv_discount.setText(data.getBill().getDiscount());
                if (data.getBill().getTotal() != null) tv_total.setText(data.getBill().getTotal());
                if (data.getBill().getTip() != null) tv_tip.setText(data.getBill().getTip());
                else tv_tip.setText("0.00");
            }

        });
    }

}

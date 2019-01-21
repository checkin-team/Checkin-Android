package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaxDiscountActivity extends AppCompatActivity {
    @BindView(R.id.tv_tax_discount_one_month)
    TextView tvTaxDiscountOneMonth;
    @BindView(R.id.tv_tax_discount)
    TextView tvTaxDiscount;
    @BindView(R.id.tv_tax_discount_save_changes)
    TextView tvTaxDiscountSaveChanges;
    @BindView(R.id.user_tax_discount_cgst)
    TextView userTaxDiscountCgst;
    @BindView(R.id.user_tax_discount_sgst)
    TextView userTaxDiscountSgst;
    @BindView(R.id.user_tax_discount_igst)
    TextView userTaxDiscountIgst;
    @BindView(R.id.user_tax_discount_gst)
    TextView userTaxDiscountGst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_discount);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
    }

    @OnClick(R.id.tv_tax_discount_save_changes)
    public void onViewClicked() {
    }
}

package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.databind.JsonNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaxDiscountActivity extends AppCompatActivity {

    public static final String KEY_SHOP_PK = "KEY_SHOP_PK";

    @BindView(R.id.tv_tax_discount_one_month)
    TextView tvTaxDiscountOneMonth;
    @BindView(R.id.et_tax_discount)
    EditText etTaxDiscount;
    @BindView(R.id.tv_tax_discount_save_changes)
    TextView tvTaxDiscountSaveChanges;
    @BindView(R.id.tv_user_tax_discount_cgst)
    TextView tvUserTaxDiscountCgst;
    @BindView(R.id.tv_user_tax_discount_sgst)
    TextView tvUserTaxDiscountSgst;
    @BindView(R.id.tv_user_tax_discount_igst)
    TextView tvUserTaxDiscountIgst;
    @BindView(R.id.tv_user_tax_discount_gst)
    TextView tvUserTaxDiscountGst;

    private FinanceViewModel mViewModel;
    private String gstin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_discount);
        ButterKnife.bind(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        String restaurantId = getIntent().getStringExtra(KEY_SHOP_PK);

        mViewModel = ViewModelProviders.of(this).get(FinanceViewModel.class);
        mViewModel.getRestaurantFinanceById(restaurantId);
        mViewModel.getRestaurantFinanceModel().observe(this,input -> {
            if (input != null && input.data == null) return;
            if (input != null && input.status == Resource.Status.SUCCESS) {

                double cgstPercent = input.data.getCgstPercent();
                double discountPercent = input.data.getDiscountPercent();
                gstin = input.data.getGstin();
                double gstPercent = input.data.getGstPercent();
                double igstPercent = input.data.getIgstPercent();
                double sgstPercent = input.data.getSgstPercent();
                double totalDiscount = input.data.getTotalDiscount();

                tvTaxDiscountOneMonth.setText(String.valueOf(totalDiscount));
                etTaxDiscount.setText(String.valueOf(discountPercent));
                tvUserTaxDiscountCgst.setText(String.valueOf(cgstPercent));
                tvUserTaxDiscountSgst.setText(String.valueOf(sgstPercent));
                tvUserTaxDiscountIgst.setText(String.valueOf(igstPercent));
                tvUserTaxDiscountGst.setText(String.valueOf(gstPercent));
            }
        });

        mViewModel.getUpdateFinanceData().observe(this, input -> {
            if (input == null) return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                Util.toast(this, input.data.getDetail());
            } else if (input.status == Resource.Status.ERROR_INVALID_REQUEST) {
                JsonNode error = input.getErrorBody();
                if (error != null && error.has("discount_percent")) {
                    JsonNode discountPercent = error.get("discount_percent");
                    String msg = discountPercent.get(0).asText();
                    Util.toast(this, msg);
                }
            }
        });
    }

    @OnClick({R.id.et_tax_discount, R.id.tv_tax_discount_save_changes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_tax_discount:
                if (!etTaxDiscount.isCursorVisible()) {
                    etTaxDiscount.setCursorVisible(true);
                }
                break;
            case R.id.tv_tax_discount_save_changes:
                String discountPercent = etTaxDiscount.getText().toString();
                try{
                    double discount = Double.valueOf(discountPercent);
                    mViewModel.updateDiscountPercent(discount);
                }catch (Exception ex){
                    Log.d("Parse Exception",ex.getLocalizedMessage());
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

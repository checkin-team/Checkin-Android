package com.checkin.app.checkin.Shop.Private.Finance;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.databind.JsonNode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinanceDetailActivity extends AppCompatActivity {

    public static final String KEY_SHOP_PK = "KEY_SHOP_PK";

    @BindView(R.id.tv_finance_total_discount)
    TextView tvTaxDiscountOneMonth;
    @BindView(R.id.et_finance_discount)
    EditText etTaxDiscount;
    @BindView(R.id.btn_finance_save)
    Button tvTaxDiscountSaveChanges;
    @BindView(R.id.tv_finance_tax_cgst)
    TextView tvUserTaxDiscountCgst;
    @BindView(R.id.tv_finance_tax_sgst)
    TextView tvUserTaxDiscountSgst;
    @BindView(R.id.tv_finance_tax_igst)
    TextView tvUserTaxDiscountIgst;
    @BindView(R.id.tv_finance_tax_gst)
    TextView tvUserTaxDiscountGst;

    private FinanceViewModel mViewModel;
    private String gstin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_detail);
        ButterKnife.bind(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        long restaurantId = getIntent().getLongExtra(KEY_SHOP_PK, 0);

        mViewModel = ViewModelProviders.of(this).get(FinanceViewModel.class);
        mViewModel.getRestaurantFinanceById(restaurantId);
        mViewModel.getRestaurantFinanceModel().observe(this, input -> {
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
                Utils.toast(this, input.data.getDetail());
            } else if (input.status == Resource.Status.ERROR_INVALID_REQUEST) {
                JsonNode error = input.getErrorBody();
                if (error != null && error.has("discount_percent")) {
                    JsonNode discountPercent = error.get("discount_percent");
                    String msg = discountPercent.get(0).asText();
                    Utils.toast(this, msg);
                }
            }
        });
    }

    @OnClick({R.id.et_finance_discount, R.id.btn_finance_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_finance_save:
                String discountPercent = etTaxDiscount.getText().toString();
                try {
                    double discount = Double.valueOf(discountPercent);
                    mViewModel.updateDiscountPercent(discount);
                    Utils.setKeyboardVisibility(etTaxDiscount,false);
                    etTaxDiscount.setCursorVisible(false);
                } catch (NumberFormatException ex) {
                    Utils.toast(this, "Input valid percentage for discount!");
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

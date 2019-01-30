package com.checkin.app.checkin.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerSessionInvoiceActivity extends AppCompatActivity {

    @BindView(R.id.rv_invoice_ordered_items)
    RecyclerView rvInvoiceOrderedItems;
    @BindView(R.id.tv_invoice_discount_title)
    TextView tvInvoiceDiscountTitle;
    @BindView(R.id.ed_invoice_discount)
    EditText edInvoiceDiscount;
    @BindView(R.id.ll_invoice_discount)
    LinearLayout llInvoiceDiscount;
    @BindView(R.id.tv_invoice_change)
    TextView tvInvoiceChange;
    @BindView(R.id.tv_invoice_save_change)
    TextView tvInvoiceSaveChange;
    @BindView(R.id.tv_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.container_invoice_total)
    FrameLayout containerInvoiceTotal;
    @BindView(R.id.btn_invoice_request_checkout)
    TextView btnInvoiceRequestCheckout;

    public static final String KEY_SESSION = "com.checkin.app.checkin.Manager.key.session";
    public static final String TABLE_NAME = "com.checkin.app.checkin.Manager.table.name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session_invoice);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        Long keySession = intent.getLongExtra(KEY_SESSION,0L);
        String tableName = intent.getStringExtra(TABLE_NAME);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            getSupportActionBar().setTitle(tableName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick({R.id.tv_invoice_change,R.id.tv_invoice_save_change,R.id.btn_invoice_request_checkout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_invoice_change:
                setUpUi("Update Discount",true,R.drawable.bordered_card_white,View.GONE,View.VISIBLE);
                break;
            case R.id.tv_invoice_save_change:
                setUpUi("Discount",false,R.drawable.bordered_text_light_grey,View.VISIBLE,View.GONE);
                break;
            case R.id.btn_invoice_request_checkout:
                break;
        }
    }

    private void setUpUi(String title, boolean enableOrDisabled, int drawable, int v1,int v2) {
        tvInvoiceDiscountTitle.setText(title);
        edInvoiceDiscount.setEnabled(enableOrDisabled);
        edInvoiceDiscount.setBackground(ContextCompat.getDrawable(this, drawable));
        llInvoiceDiscount.setVisibility(v1);
        tvInvoiceChange.setVisibility(v1);
        tvInvoiceSaveChange.setVisibility(v2);
    }
}

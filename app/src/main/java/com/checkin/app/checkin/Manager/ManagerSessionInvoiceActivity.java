package com.checkin.app.checkin.Manager;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.InvoiceOrdersAdapter;
import com.checkin.app.checkin.Session.Model.SessionBillModel;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerSessionInvoiceActivity extends AppCompatActivity {
    public static final String KEY_SESSION = "com.checkin.app.checkin.Manager.key.session";
    public static final String TABLE_NAME = "com.checkin.app.checkin.Manager.table.name";

    @BindView(R.id.rv_ms_invoice_ordered_items)
    RecyclerView rvOrderedItems;
    @BindView(R.id.tv_invoice_discount_title)
    TextView tvInvoiceDiscountTitle;
    @BindView(R.id.ed_ms_invoice_discount)
    EditText edInvoiceDiscount;
    @BindView(R.id.tv_ms_invoice_change)
    TextView tvInvoiceChange;
    @BindView(R.id.btn_ms_invoice_save_change)
    Button btnSaveChange;
    @BindView(R.id.tv_ms_invoice_total)
    TextView tvInvoiceTotal;
    @BindView(R.id.tv_invoice_discount)
    TextView tvInvoiceDiscount;

    private ManagerSessionViewModel mViewModel;
    private InvoiceOrdersAdapter mAdapter;
    private SessionBillModel mBillModel;
    private BillHolder mBillHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_session_invoice);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        long keySession = intent.getLongExtra(KEY_SESSION, 0L);
        String tableName = intent.getStringExtra(TABLE_NAME);

        mViewModel = ViewModelProviders.of(this).get(ManagerSessionViewModel.class);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(tableName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InvoiceOrdersAdapter(null);
        rvOrderedItems.setAdapter(mAdapter);

        mViewModel.getSessionInvoice(keySession).observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                setupData(resource.data);
            }
        });
        mViewModel.getDetailData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Utils.toast(this, resource.message);
                finish();
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(this, "Error: " + resource.message);
            }
        });
        mBillHolder = new BillHolder(findViewById(android.R.id.content));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @OnClick({R.id.tv_ms_invoice_change, R.id.btn_ms_invoice_save_change, R.id.btn_ms_invoice_collect_cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_ms_invoice_change:
                setUpUi("Update Discount", true, R.drawable.bordered_card_white, View.GONE, View.VISIBLE);
                break;
            case R.id.btn_ms_invoice_save_change:
                setUpUi("Discount", false, R.drawable.bordered_text_light_grey, View.VISIBLE, View.GONE);
                updateDiscount();
                break;
            case R.id.btn_ms_invoice_collect_cash:
                approveBill();
                break;
        }
    }

    private void approveBill() {
        double percent = mBillModel.getDiscountPercentage();
        mViewModel.approveBill(percent);
    }

    private void updateDiscount() {
        Double percent = 0d;
        try {
            percent = Double.valueOf(edInvoiceDiscount.getText().toString());
        } catch (NumberFormatException ignored) {
        }
        mBillModel.calculateDiscount(percent);
        tvInvoiceDiscount.setText(Utils.formatCurrencyAmount(this, mBillModel.getDiscount()));
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, mBillModel.getTotal()));
    }

    private void setupData(ManagerSessionInvoiceModel data) {
        mBillModel = data.getBill();

        mBillModel.setDiscountPercentage(data.getDiscountPercent());
        mAdapter.setData(data.getOrderedItems());
        edInvoiceDiscount.setText(data.formatDiscountPercent());
        mBillHolder.bind(data.getBill());
        tvInvoiceTotal.setText(Utils.formatCurrencyAmount(this, data.getBill().getTotal()));

        setUpUi("Discount", false, R.drawable.bordered_text_light_grey, View.VISIBLE, View.GONE);
    }

    private void setUpUi(String title, boolean enableOrDisabled, int drawable, int visibilityChange, int visibilitySave) {
        tvInvoiceDiscountTitle.setText(title);
        edInvoiceDiscount.setEnabled(enableOrDisabled);
        edInvoiceDiscount.setBackground(ContextCompat.getDrawable(this, drawable));
        tvInvoiceChange.setVisibility(visibilityChange);
        btnSaveChange.setVisibility(visibilitySave);
    }
}

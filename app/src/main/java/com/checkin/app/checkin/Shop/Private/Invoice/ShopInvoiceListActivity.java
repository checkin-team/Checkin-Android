package com.checkin.app.checkin.Shop.Private.Invoice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.misc.activities.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInvoiceListActivity extends BaseActivity implements ShopInvoiceSessionAdapter.ShopInvoiceInteraction {
    public static final String KEY_SHOP_PK = "SHOP_KEY";

    @BindView(R.id.tv_shop_invoice_filter_from)
    TextView tvFilterFrom;
    @BindView(R.id.tv_shop_invoice_filter_to)
    TextView tvFilterTo;
    @BindView(R.id.rv_shop_invoice_sessions)
    RecyclerView rvSessions;
    @BindView(R.id.tv_invoice_total_sales)
    TextView tvTotalSales;
    @BindView(R.id.tv_invoice_total_discount)
    TextView tvTotalDiscounts;
    @BindView(R.id.tv_invoice_total_orders)
    TextView tvTotalOrders;
    @BindView(R.id.tv_invoice_total_tax)
    TextView tvTotalTaxes;

    private ShopInvoiceSessionAdapter mAdapter;
    private ShopInvoiceViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice_list);
        ButterKnife.bind(this);

        initProgressBar(R.id.pb_shop_invoice);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        mAdapter = new ShopInvoiceSessionAdapter(this);
        mViewModel = ViewModelProviders.of(this).get(ShopInvoiceViewModel.class);
        mViewModel.setDoFetchStats(true);

        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopSessions(shopPk);
        mViewModel.fetchShopStats(shopPk);

        mViewModel.getRestaurantSessions().observe(this, input -> {
            if (input == null)
                return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                mAdapter.setSessionData(input.getData());
                hideProgressBar();
            } else if (input.getStatus() != Resource.Status.LOADING) {
                Utils.toast(this, input.getMessage());
                hideProgressBar();
            }
            if (input.getStatus() == Resource.Status.LOADING)
                visibleProgressBar();
        });
        mViewModel.getRestaurantStats().observe(this, input -> {
            if (input == null) return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null)
                updateStatsUi(input.getData());
            else if (input.getStatus() != Resource.Status.LOADING)
                Utils.toast(this, input.getMessage());
        });
        setupUi();
    }

    private void updateStatsUi(RestaurantAdminStatsModel data) {
        tvTotalSales.setText(Utils.formatIntegralCurrencyAmount(this, data.getTotalSales()));
        tvTotalDiscounts.setText(Utils.formatIntegralCurrencyAmount(this, data.getTotalDiscounts()));
        tvTotalOrders.setText(data.formatTotalOrders());
        tvTotalTaxes.setText(Utils.formatIntegralCurrencyAmount(this, data.getTotalTaxes()));
    }

    private void setupUi() {
        rvSessions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvSessions.setAdapter(mAdapter);

        tvFilterFrom.setText("------------");
        String toDate = Utils.getCurrentFormattedDate();
        tvFilterTo.setText(toDate);
//        mViewModel.filterTo(toDate);
    }

    private String updateDate(TextView tvDate, int year, int month, int day) {
        String initialDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month, day);
        try {
            String result = Utils.convertFormatDate(initialDate, "yyyy-MM-dd", "MMM dd, yyyy");
            tvDate.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return initialDate;
    }

    @OnClick({R.id.tv_shop_invoice_filter_from, R.id.tv_shop_invoice_filter_to})
    public void onClick(TextView tv) {
        Calendar cal = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(tv.getText().toString());
            cal.setTime(date);
        } catch (ParseException e) {
        }
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String resultDate = updateDate(tv, year, month + 1, dayOfMonth);
            if (tv.getId() == R.id.tv_shop_invoice_filter_from)
                mViewModel.filterFrom(resultDate);
            else
                mViewModel.filterTo(resultDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClickSession(RestaurantSessionModel data) {
        Intent intent = new Intent(this, ShopInvoiceDetailActivity.class);
        intent.putExtra(ShopInvoiceDetailActivity.KEY_SESSION_DATA, data);
        startActivity(intent);
    }
}

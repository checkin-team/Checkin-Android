package com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
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

public class ShopInvoiceListActivity extends AppCompatActivity implements ShopInvoiceSessionAdapter.ShopInvoiceInteraction {
    public static final String KEY_SHOP_PK = "SHOP_KEY";

    @BindView(R.id.tv_shop_invoice_filter_from)
    TextView tvFilterFrom;
    @BindView(R.id.tv_shop_invoice_filter_to)
    TextView tvFilterTo;
    @BindView(R.id.rv_shop_invoice_sessions)
    RecyclerView rvSessions;

    private ShopInvoiceSessionAdapter mAdapter;
    private ShopInvoiceViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        mAdapter = new ShopInvoiceSessionAdapter(this);
        mViewModel = ViewModelProviders.of(this).get(ShopInvoiceViewModel.class);

        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopSessions(shopPk);

        mViewModel.getRestaurantSessions().observe(this, input->{
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data !=  null){
                mAdapter.setSessionData(input.data);
            }
        });
        setupUi();
    }

    private void setupUi() {
        rvSessions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSessions.setAdapter(mAdapter);

        tvFilterFrom.setText(Utils.getCurrentFormattedDate());
        tvFilterTo.setText(Utils.getCurrentFormattedDate());
    }

    private String updateDate(TextView tvDate, int year, int month, int day) {
        String initialDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month, day);
        try {
            String result = Utils.formatDate(initialDate, "yyyy-MM-dd", "MMM dd, yyyy");
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

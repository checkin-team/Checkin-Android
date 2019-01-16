package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.Utility.Util.getCurrentFormattedDate;
import static com.checkin.app.checkin.Utility.Util.getFormattedSelectedDate;

public class ShopInvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_SHOP_PK = "SHOP_KEY";

    @BindView(R.id.tv_shop_invoice_from_title)
    TextView tvShopInvoiceFromTitle;
    @BindView(R.id.tv_shop_invoice_from_date)
    TextView tvShopInvoiceFromDate;
    @BindView(R.id.cv_shop_invoice_from_date)
    CardView cvShopInvoiceFromDate;
    @BindView(R.id.tv_shop_invoice_to_title)
    TextView tvShopInvoiceToTitle;
    @BindView(R.id.tv_shop_invoice_to_date)
    TextView tvShopInvoiceToDate;
    @BindView(R.id.cv_shop_invoice_to_date)
    CardView cvShopInvoiceToDate;
    @BindView(R.id.rv_shop_invoice)
    RecyclerView rvShopInvoice;

    private int year, month, day;
    private ShopInvoiceAdapter shopInvoiceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_invoice);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        this.shopInvoiceAdapter = new ShopInvoiceAdapter();
        rvShopInvoice.setLayoutManager(new LinearLayoutManager(this));
        rvShopInvoice.setAdapter(shopInvoiceAdapter);
        rvShopInvoice.setItemAnimator(new DefaultItemAnimator());

        String mShopKey = getIntent().getStringExtra(KEY_SHOP_PK);

        if (mShopKey != null){
            ShopInvoiceViewModel mModel = ViewModelProviders.of(this).get(ShopInvoiceViewModel.class);
            mModel.getRestaurantSessionsById(mShopKey);

            mModel.getRestaurantSessions().observe(this, input->{
                if (input == null)
                    return;
                if (input.status == Resource.Status.SUCCESS && input.data !=  null){
                    shopInvoiceAdapter.addSessionData(input.data);
                }
            });
        }

        cvShopInvoiceFromDate.setOnClickListener(this);
        cvShopInvoiceToDate.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        setMyInitial(c);
    }

    private void setMyInitial(Calendar c) {
        tvShopInvoiceFromDate.setText(getCurrentFormattedDate(c));
        tvShopInvoiceToDate.setText(getCurrentFormattedDate(c));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_shop_invoice_from_date:
                setInvoiceDate(tvShopInvoiceFromDate);
                break;
            case R.id.cv_shop_invoice_to_date:
                setInvoiceDate(tvShopInvoiceToDate);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setInvoiceDate(TextView tvShopInvoiceDate) {
        new DatePickerDialog(this, (datePicker, i, i1, i2) -> {

            int mDay = datePicker.getDayOfMonth();
            int mMonth = datePicker.getMonth();
            int mYear = datePicker.getYear();

            String mInitDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", mYear, mMonth + 1, mDay);
            String finalDate;

            try {
                finalDate = getFormattedSelectedDate(mInitDate, "yyyy-MM-dd", "MMM dd, yyyy");
                tvShopInvoiceDate.setText(finalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, year, month, day).show();
    }
}


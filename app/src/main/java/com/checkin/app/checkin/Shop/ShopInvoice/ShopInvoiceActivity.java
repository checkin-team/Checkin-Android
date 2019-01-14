package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.text.ParseException;
import java.util.Calendar;

import butterknife.ButterKnife;

import static com.checkin.app.checkin.Utility.Util.getCurrentFormattedDate;
import static com.checkin.app.checkin.Utility.Util.getFormattedSelectedDate;

public class ShopInvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvShopInvoiceFromTitle;
    TextView tvShopInvoiceFromDate;
    CardView cvShopInvoiceFromDate;
    TextView tvShopInvoiceToTitle;
    TextView tvShopInvoiceToDate;
    CardView cvShopInvoiceToDate;
    RecyclerView rvShopInvoice;

    private int year,month,day;

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

        tvShopInvoiceFromTitle = findViewById(R.id.tv_shop_invoice_from_title);
        tvShopInvoiceFromDate = findViewById(R.id.tv_shop_invoice_from_date);
        cvShopInvoiceFromDate = findViewById(R.id.cv_shop_invoice_from_date);
        tvShopInvoiceToTitle = findViewById(R.id.tv_shop_invoice_to_title);
        tvShopInvoiceToDate = findViewById(R.id.tv_shop_invoice_to_date);
        cvShopInvoiceToDate = findViewById(R.id.cv_shop_invoice_to_date);
        rvShopInvoice = findViewById(R.id.rv_shop_invoice);

        cvShopInvoiceFromDate.setOnClickListener(this);
        cvShopInvoiceToDate.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        setMyInitial(c);

        ShopInvoiceAdapter shopInvoiceAdapter = new ShopInvoiceAdapter();

        rvShopInvoice.setLayoutManager(new LinearLayoutManager(this));
        rvShopInvoice.setAdapter(shopInvoiceAdapter);
        rvShopInvoice.setItemAnimator(new DefaultItemAnimator());
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

    private void setInvoiceDate(TextView tvShopInvoiceDate) {
        new DatePickerDialog(this, (datePicker, i, i1, i2) -> {

            int mDay = datePicker.getDayOfMonth();
            int mMonth = datePicker.getMonth();
            int mYear = datePicker.getYear();

            String mInitDate = mDay+"/"+(mMonth + 1)+"/"+mYear;

            String finalDate;

            try {
                finalDate = getFormattedSelectedDate(mInitDate,"dd/MM/yyyy","MMM dd, yyyy");
                tvShopInvoiceDate.setText(finalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, year, month, day).show();
    }
}


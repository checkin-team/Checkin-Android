package com.checkin.app.checkin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.checkin.app.checkin.Shop.ShopInvoice.ShopInvoiceFeedbackAdapter;

public class DemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        RecyclerView rvShopInvoiceFeedback = findViewById(R.id.rvShopInvoiceFeedback);

        ShopInvoiceFeedbackAdapter shopInvoiceFeedbackAdapter = new ShopInvoiceFeedbackAdapter();
        rvShopInvoiceFeedback.setLayoutManager(new LinearLayoutManager(this));
        rvShopInvoiceFeedback.setAdapter(shopInvoiceFeedbackAdapter);
    }
}

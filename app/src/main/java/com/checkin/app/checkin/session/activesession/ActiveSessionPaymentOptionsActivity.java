package com.checkin.app.checkin.session.activesession;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.instamojo.android.Instamojo;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionPaymentOptionsActivity extends BaseActivity {

    @BindView(R.id.tv_as_payment_options_amount)
    TextView tvAmount;

    public static final String KEY_SESSION_AMOUNT = "session.total_amount";
    public static final String KEY_PAYMENT_MODE_RESULT = "payment_mode.result";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_payment_options);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvAmount.setText(getIntent().getStringExtra(KEY_SESSION_AMOUNT));
    }

    @OnClick(R.id.cash_container)
    public void onCashClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, ShopModel.PAYMENT_MODE.CASH);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.paytm_container)
    public void onPaytmClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, ShopModel.PAYMENT_MODE.PAYTM);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.instamojo_container)
    public void onInstamojoClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, ShopModel.PAYMENT_MODE.INSTAMOJO);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

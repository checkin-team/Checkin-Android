package com.checkin.app.checkin.Session.ActiveSession;

import android.content.Intent;
import android.os.Bundle;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBillModel;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionPaymentOptions extends BaseActivity {

    public static final String KEY_PAYMENT_MODE_RESULT = "payment_mode.result";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_payment_opytions);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    @OnClick(R.id.cash_container)
    public void onCashClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, SessionBillModel.PAYMENT_MODES.CASH);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.paytm_container)
    public void onPaytmClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, SessionBillModel.PAYMENT_MODES.PAYTM);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.gpay_container)
    public void onGPayClick(){
        Intent data = new Intent();
        data.putExtra(KEY_PAYMENT_MODE_RESULT, SessionBillModel.PAYMENT_MODES.GOOGLE_PAY);
        setResult(RESULT_OK, data);
        finish();
    }


}

package com.checkin.app.checkin.Session.ActiveSession;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.R;

import butterknife.ButterKnife;

public class ActiveSessionBillDetails extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_checkout);
        ButterKnife.bind(this);
    }
}

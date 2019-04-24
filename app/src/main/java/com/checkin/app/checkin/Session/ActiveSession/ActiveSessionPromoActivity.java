package com.checkin.app.checkin.Session.ActiveSession;

import android.os.Bundle;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionPromoActivity extends BaseActivity {

    @BindView(R.id.rv_available_promos)
    RecyclerView rvPromos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_promo);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }
}

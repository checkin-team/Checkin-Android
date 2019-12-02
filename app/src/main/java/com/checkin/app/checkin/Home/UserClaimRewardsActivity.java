package com.checkin.app.checkin.Home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;

import butterknife.OnClick;

public class UserClaimRewardsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user_claim_rewards);
    }

    @OnClick(R.id.im_home_brownie_cash_back)
    public void onBackPress() {
        onBackPressed();

    }


}
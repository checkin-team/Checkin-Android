package com.checkin.app.checkin.ManagerProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveOrdersActivity extends AppCompatActivity {

    @BindView(R.id.vp_tl_live_orders)
    ViewPager vpTlLiveOrders;
    @BindView(R.id.tl_live_orders)
    TabLayout tlLiveOrders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_orders);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        setUpWithMyViewPager(vpTlLiveOrders);
        tlLiveOrders.setupWithViewPager(vpTlLiveOrders,true);
        tlLiveOrders.getTabAt(0).setIcon(R.drawable.ic_live_orders);
        tlLiveOrders.getTabAt(1).setIcon(R.drawable.ic_stats);
    }

    private void setUpWithMyViewPager(ViewPager vpTlLiveOrders) {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(ShopManagerTableFragment.newInstance());
        fragmentList.add(ShopManagerTableStaticsFragment.newInstance());

        LiveOrdersPagerAdapter liveOrdersPagerAdapter = new LiveOrdersPagerAdapter(fragmentList,getSupportFragmentManager());
        vpTlLiveOrders.setAdapter(liveOrdersPagerAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

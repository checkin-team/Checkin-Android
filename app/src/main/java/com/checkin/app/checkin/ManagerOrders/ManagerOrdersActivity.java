package com.checkin.app.checkin.ManagerOrders;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerOrdersActivity extends AppCompatActivity implements ManagerOrdersFragment.ManagerOrdersInteraction {
    @BindView(R.id.im_swipe_up)
    ImageView imSwipeUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_orders);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.im_swipe_up)
    public void onSwipeUp(){
        setupOrdersListing();
    }


    private void setupOrdersListing() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_orders_fragment, ManagerOrdersFragment.newInstance(this),"manager_orders")
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag("manager_orders");
        if (mFragment != null) {
            ((ManagerOrdersFragment) mFragment).onBackPressed();
        }else super.onBackPressed();
    }

    @OnClick(R.id.btn_back)
    public void goBack(View v){
        onBackPressed();
    }
}

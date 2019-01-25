package com.checkin.app.checkin.ManagerOrders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagerOrdersActivity extends AppCompatActivity implements ManagerOrdersFragment.ManagerOrdersInteraction {
    private ManagerOrdersFragment mFilterFragment;
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
        mFilterFragment = ManagerOrdersFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_orders_fragment, mFilterFragment)
                .setCustomAnimations(R.anim.slide_up,R.anim.slide_down)
                .commit();
    }
}

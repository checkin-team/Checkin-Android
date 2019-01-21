package com.checkin.app.checkin.User.NonPersonalProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCheckinsActivity extends AppCompatActivity {
    @BindView(R.id.rv_user_checkin_restaurant)
    RecyclerView rvUserCheckRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkins);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        UserCheckinAdapter userRestaurantAdapter = new UserCheckinAdapter();
        rvUserCheckRestaurant.setLayoutManager(new GridLayoutManager(this,2));
        rvUserCheckRestaurant.setAdapter(userRestaurantAdapter);
        rvUserCheckRestaurant.addItemDecoration(new MyItemDecorator(15));
        rvUserCheckRestaurant.setItemAnimator(new DefaultItemAnimator());

        UserViewModel mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        //mUserViewModel.getUsercheckinById("1");
        mUserViewModel.dummyCheckins();
        mUserViewModel.getUsercheckinModel().observe(this, input ->{
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null && input.data.size() > 0){
                userRestaurantAdapter.addUserCheckinData(input.data);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

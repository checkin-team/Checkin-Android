package com.checkin.app.checkin.User.Public;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopPublicProfile.ShopActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCheckinsActivity extends AppCompatActivity implements UserCheckinAdapter.UserCheckinShopInteraction {

    public static final String KEY_USER_PK = "KEY_USER_PK";

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

        long userId = getIntent().getLongExtra(KEY_USER_PK, 0);

        UserCheckinAdapter userRestaurantAdapter = new UserCheckinAdapter(this);
        rvUserCheckRestaurant.setLayoutManager(new GridLayoutManager(this, 2));
        rvUserCheckRestaurant.setAdapter(userRestaurantAdapter);

        UserViewModel mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.fetchUserCheckins(userId);
        mUserViewModel.getUserCheckinsData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                userRestaurantAdapter.setData(input.data);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClickShop(BriefModel shop) {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(ShopActivity.KEY_SHOP_PK, Long.valueOf(shop.getPk()));
        startActivity(intent);
    }
}

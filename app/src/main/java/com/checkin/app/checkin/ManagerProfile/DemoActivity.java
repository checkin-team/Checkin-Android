package com.checkin.app.checkin.ManagerProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DemoActivity extends AppCompatActivity {
    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);

        ShopManagerTableAdapter shopManagerTableAdapter = new ShopManagerTableAdapter();
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(this));
        rvShopManagerTable.setItemAnimator(new DefaultItemAnimator());
        rvShopManagerTable.setAdapter(shopManagerTableAdapter);

        RestaurantTableViewModel restaurantTableViewModel = ViewModelProviders.of(this).get(RestaurantTableViewModel.class);
        restaurantTableViewModel.getRestaurantTableById("3");
        restaurantTableViewModel.getRestaurantTableModel().observe(this, input->{
            if (input != null && input.data == null)
                return;
            if (input != null && input.data.size() > 0 && input.status == Resource.Status.SUCCESS) {
                shopManagerTableAdapter.setRestaurantTableList(input.data);
            }
        });
    }
}

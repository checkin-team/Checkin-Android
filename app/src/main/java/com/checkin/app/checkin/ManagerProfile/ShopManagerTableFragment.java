package com.checkin.app.checkin.ManagerProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopManagerTableFragment extends Fragment {
    @BindView(R.id.rv_shop_manager_table)
    RecyclerView rvShopManagerTable;
    Unbinder unbinder;

    public ShopManagerTableFragment() {
    }

    public ShopManagerTableFragment newInstance() {
        return new ShopManagerTableFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShopManagerTableAdapter shopManagerTableAdapter = new ShopManagerTableAdapter();
        rvShopManagerTable.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

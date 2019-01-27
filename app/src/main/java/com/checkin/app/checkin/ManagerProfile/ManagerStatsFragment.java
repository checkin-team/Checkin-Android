package com.checkin.app.checkin.ManagerProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ManagerStatsFragment extends Fragment {

    @BindView(R.id.cv_shop_manager_table_statics)
    CardView cvShopManagerTableStatics;
    @BindView(R.id.tv_shop_manger_table_statics_title)
    TextView tvShopMangerTableStaticsTitle;
    @BindView(R.id.rv_shop_manager_table_statics)
    RecyclerView rvShopManagerTableStatics;
    Unbinder unbinder;

    public ManagerStatsFragment() {
    }

    public static ManagerStatsFragment newInstance() {
        return new ManagerStatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_manager_table_statics, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopManagerTableStaticsAdapter shopManagerTableStaticsAdapter = new ShopManagerTableStaticsAdapter();
        rvShopManagerTableStatics.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShopManagerTableStatics.setAdapter(shopManagerTableStaticsAdapter);
        rvShopManagerTableStatics.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

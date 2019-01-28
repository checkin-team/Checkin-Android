package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import java.util.List;

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
    @BindView(R.id.tv_shop_manager_table_statics_title)
    TextView tvShopManagerTableStaticsTitle;
    @BindView(R.id.tv_shop_manager_table_day_revenue)
    TextView tvShopManagerTableDayRevenue;
    @BindView(R.id.tv_shop_manager_table_week_revenue)
    TextView tvShopManagerTableWeekRevenue;
    @BindView(R.id.tv_shop_manager_table_session_time)
    TextView tvShopManagerTableSessionTime;
    @BindView(R.id.tv_shop_manager_table_serving_time)
    TextView tvShopManagerTableServingTime;
    private Unbinder unbinder;

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

        ManagerStatsOrderAdapter managerStatsOrderAdapter = new ManagerStatsOrderAdapter();
        rvShopManagerTableStatics.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShopManagerTableStatics.setAdapter(managerStatsOrderAdapter);

        ManagerWorkViewModel managerWorkViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        //managerWorkViewModel.fetchStatistics();
        managerWorkViewModel.dummyData();
        managerWorkViewModel.getRestaurantStatics().observe(this, input -> {
            if (input != null && input.data == null)
                return;
            if (input != null && input.status == Resource.Status.SUCCESS) {
                RestaurantStaticsModel mModel = input.data;
                String sessionTime = mModel.getAvgSessionTime();
                String servingTime = mModel.getAvgServingTime();
                String daysRevenue = mModel.getDaysRevenue();
                String weekRevenue = mModel.getWeeksRevenue();
                tvShopManagerTableDayRevenue.setText(daysRevenue);
                tvShopManagerTableWeekRevenue.setText(weekRevenue);
                tvShopManagerTableSessionTime.setText(String.valueOf(sessionTime));
                tvShopManagerTableServingTime.setText(String.valueOf(servingTime));
                List<RestaurantStaticsModel.TrendingOrder> trendingOrder = mModel.getTrendingOrders();
                if (trendingOrder != null && trendingOrder.size() > 0) {
                    managerStatsOrderAdapter.setRestaurantStaticsList(trendingOrder);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

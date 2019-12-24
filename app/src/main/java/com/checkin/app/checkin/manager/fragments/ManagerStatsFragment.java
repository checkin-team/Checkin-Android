package com.checkin.app.checkin.manager.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.manager.adapters.ManagerStatsOrderAdapter;
import com.checkin.app.checkin.manager.models.ManagerStatsModel;
import com.checkin.app.checkin.manager.viewmodels.ManagerWorkViewModel;
import com.checkin.app.checkin.misc.fragments.BaseFragment;

import java.util.Locale;

import butterknife.BindView;

public class ManagerStatsFragment extends BaseFragment {

    @BindView(R.id.rv_manager_stats_trending_orders)
    RecyclerView rvTrendingOrders;
    @BindView(R.id.tv_manager_stats_revenue_day)
    TextView tvDayRevenue;
    @BindView(R.id.tv_manager_stats_revenue_week)
    TextView tvWeekRevenue;
    @BindView(R.id.tv_manager_stats_orders_day)
    TextView tvDayOrders;
    @BindView(R.id.tv_manager_stats_orders_week)
    TextView tvWeekOrders;
    @BindView(R.id.tv_manager_stats_session_time)
    TextView tvSessionTime;
    @BindView(R.id.tv_manager_stats_serving_time)
    TextView tvServingTime;

    private ManagerStatsOrderAdapter mAdapter;
    private ManagerWorkViewModel mViewModel;

    public ManagerStatsFragment() {
    }

    public static ManagerStatsFragment newInstance() {
        return new ManagerStatsFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_shop_manager_statistics;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mAdapter = new ManagerStatsOrderAdapter();
        rvTrendingOrders.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvTrendingOrders.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ManagerWorkViewModel.class);
        mViewModel.fetchStatistics();
        mViewModel.getRestaurantStatistics().observe(this, input -> {
            if (input == null)
                return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                setupData(input.getData());
            } else if (input.getStatus() != Resource.Status.LOADING) {
                Utils.toast(requireContext(), input.getMessage());
            }
        });
    }

    private void setupData(@NonNull ManagerStatsModel data) {
        tvDayOrders.setText(String.format(Locale.ENGLISH, "%s orders", data.getDayOrdersCount()));
        tvWeekOrders.setText(String.format(Locale.ENGLISH, "%s orders", data.getWeekOrdersCount()));
        tvDayRevenue.setText(Utils.formatCurrencyAmount(getContext(), data.getDayRevenue()));
        tvWeekRevenue.setText(Utils.formatCurrencyAmount(getContext(), data.getWeekRevenue()));
        tvSessionTime.setText(data.formatAvgSessionTime());
        tvServingTime.setText(data.formatAvgServingTime());

        mAdapter.setData(data.getTrendingOrders());
    }
}

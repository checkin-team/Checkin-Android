package com.checkin.app.checkin.Shop.Private.Insight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Manager.Adapter.ManagerStatsOrderAdapter;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopInsightRevenueFragment extends BaseFragment {
    @BindView(R.id.vp_shop_insight_revenue)
    ViewPager vpRevenue;
    @BindView(R.id.indicator_shop_insight_revenue)
    PageIndicatorView indicatorRevenue;
    @BindView(R.id.tv_shop_insight_revenue_floating_cash)
    TextView tvFloatingCash;
    @BindView(R.id.tv_shop_insight_revenue_cancellation_rate)
    TextView tvCancellationRate;
    @BindView(R.id.rv_shop_insight_revenue_trending_orders)
    RecyclerView rvTrendingOrders;

    private ShopInsightRevenueAdapter mPagerAdapter;
    private ManagerStatsOrderAdapter mAdapter;
    private ShopInsightViewModel mViewModel;

    public ShopInsightRevenueFragment() {
    }

    public static ShopInsightRevenueFragment newInstance() {
        return new ShopInsightRevenueFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_shop_insight_revenue;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi();
        getData();
    }

    private void setUpUi() {
        mPagerAdapter = new ShopInsightRevenueAdapter(null);
        vpRevenue.setAdapter(mPagerAdapter);
        indicatorRevenue.setViewPager(vpRevenue);
        indicatorRevenue.setAnimationType(AnimationType.FILL);
        indicatorRevenue.setClickListener(position -> vpRevenue.setCurrentItem(position));
        mAdapter = new ManagerStatsOrderAdapter();
        rvTrendingOrders.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvTrendingOrders.setAdapter(mAdapter);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopInsightViewModel.class);
        mViewModel.getInsightRevenueDetail().observe(this, shopInsightRevenueModelResource -> {
            if (shopInsightRevenueModelResource == null)
                return;
            if (shopInsightRevenueModelResource.getStatus() == Resource.Status.SUCCESS && shopInsightRevenueModelResource.getData() != null) {
                ShopInsightRevenueModel revenueModel = shopInsightRevenueModelResource.getData();
                mPagerAdapter.setData(revenueModel);
                tvFloatingCash.setText(Utils.formatCurrencyAmount(requireContext(), revenueModel.getFloatingCash()));
                tvCancellationRate.setText(revenueModel.getCancellationRate());
                mAdapter.setData(revenueModel.getTrendingOrders());
            }
        });
    }

    public class ShopInsightRevenueAdapter extends PagerAdapter {
        private ShopInsightRevenueModel mData;

        public ShopInsightRevenueAdapter(ShopInsightRevenueModel data) {
            this.mData = data;
        }

        public void setData(ShopInsightRevenueModel data) {
            mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            View view;
            ViewHolder holder;

            view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_shop_insight_revenue_amount, null);
            holder = new ViewHolder(view);

            if (mData != null)
                holder.bindData(mData, position);

            container.addView(view, 0);
            return view;

        }

        @Override
        public int getCount() {
            return mData != null ? 3 : 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        class ViewHolder {
            @BindView(R.id.tv_shop_insight_revenue_duration)
            TextView tvDuration;
            @BindView(R.id.tv_shop_insight_revenue_orders_number)
            TextView tvOrdersNumber;
            @BindView(R.id.tv_shop_insight_revenue_amount)
            TextView tvAmount;
            ShopInsightRevenueModel mData;

            ViewHolder(@NonNull View view) {
                ButterKnife.bind(this, view);
            }

            void bindData(ShopInsightRevenueModel data, int position) {
                mData = data;
                switch (position) {
                    case 0:
                        tvDuration.setText("Today's Revenue");
                        tvOrdersNumber.setText(String.format(Locale.ENGLISH, "%.0f ORDERS", data.getCountOrders().getDay()));
                        tvAmount.setText(Utils.formatCurrencyAmount(tvAmount.getContext(), data.getRevenue().getDay()));
                        break;
                    case 1:
                        tvDuration.setText("Week's Revenue");
                        tvOrdersNumber.setText(String.format(Locale.ENGLISH, "%.0f ORDERS", data.getCountOrders().getWeek()));
                        tvAmount.setText(Utils.formatCurrencyAmount(tvAmount.getContext(), data.getRevenue().getWeek()));
                        break;
                    case 2:
                        tvDuration.setText("Month's Revenue");
                        tvOrdersNumber.setText(String.format(Locale.ENGLISH, "%.0f ORDERS", data.getCountOrders().getMonth()));
                        tvAmount.setText(Utils.formatCurrencyAmount(tvAmount.getContext(), data.getRevenue().getMonth()));
                        break;
                }
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(((View) object));
        }
    }
}

package com.checkin.app.checkin.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class UserHomeFragment extends BaseFragment {

    @BindView(R.id.rv_user_private_recent_shops)
    RecyclerView rvTrendingRestaurants;
    @BindView(R.id.indicator_home_banner)
    PageIndicatorView indicatorView;
    @BindView(R.id.vp_home_banner)
    ViewPager vpBanner;
    private UserHomeTrendingRestaurantAdapter mTrendingRestAdapter;
    private BannerPagerAdapter mPagerAdapter;

    public UserHomeFragment() {
    }

    public static UserHomeFragment newInstance() {
        return new UserHomeFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_user_home;
    }

    @OnClick(R.id.container_home_brownie_cash)
    public void brownieClick() {
        startActivity(new Intent(requireActivity(), UserBrownieCashActivity.class));
    }

    @OnClick(R.id.container_home_claim_rewards)
    public void claimRewardsClick() {
        startActivity(new Intent(requireActivity(), UserClaimRewardsActivity.class));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);

        mPagerAdapter = new BannerPagerAdapter(requireActivity());
        vpBanner.setAdapter(mPagerAdapter);
        indicatorView.setViewPager(vpBanner);
        indicatorView.setAnimationType(AnimationType.FILL);
        indicatorView.setClickListener(position -> vpBanner.setCurrentItem(position));

        rvTrendingRestaurants.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
        mTrendingRestAdapter = new UserHomeTrendingRestaurantAdapter();
        rvTrendingRestaurants.setAdapter(mTrendingRestAdapter);
        rvTrendingRestaurants.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE)
                    enableDisableSwipeRefresh(false);
                else
                    enableDisableSwipeRefresh(true);
            }
        });

        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

    }

    public void enableDisableSwipeRefresh(boolean enable) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(enable);
    }

    class BannerPagerAdapter extends PagerAdapter {

        int[] mResources = {R.drawable.first_banner, R.drawable.second_banner, R.drawable.third_banner};

        Context mContext;
        LayoutInflater mLayoutInflater;

        public BannerPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_home_banner, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.im_home_banner);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);

            return itemView;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(((View) object));
        }

    }


}

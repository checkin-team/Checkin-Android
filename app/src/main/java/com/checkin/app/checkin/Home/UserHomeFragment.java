package com.checkin.app.checkin.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.Objects;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.OnClick;

public class UserHomeFragment extends BaseFragment {

    @BindView(R.id.rv_user_private_recent_shops)
    RecyclerView rvTrendingRestaurants;
//    @BindView(R.id.indicator_home_banner)
//    PageIndicatorView indicatorView;
//    @BindView(R.id.vp_home_banner)
//    ViewPager vpBanner;
    private UserHomeTrendingRestaurantAdapter mTrendingRestAdapter;

    private NearbyRestaurantAdapter mRestAdapter;
    @BindView(R.id.container_advertisement_nearbyResturant)
     RecyclerView recyclerView;

    private HomeViewModel mViewModel;

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
        mViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);

        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);

        mRestAdapter = new NearbyRestaurantAdapter(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mRestAdapter);
//        vpBanner.setAdapter(mPagerAdapter);
//        indicatorView.setViewPager(vpBanner);
//        indicatorView.setAnimationType(AnimationType.FILL);
//        indicatorView.setClickListener(position -> vpBanner.setCurrentItem(position));

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

//        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
//            }
//        });

        mViewModel.getNearbyRestaurantData().observe(this, listResource -> {
            if (listResource == null) return;
            else{
                mRestAdapter.updateData(listResource.getData());
            }
        });

    }

    public void enableDisableSwipeRefresh(boolean enable) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(enable);
    }

}

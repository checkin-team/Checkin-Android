package com.checkin.app.checkin.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.ActiveSessionMemberAdapter;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class UserHomeFragment extends BaseFragment {

    @BindView(R.id.rv_user_private_recent_shops)
    RecyclerView rvTrendingRestaurants;
    private UserHomeTrendingRestaurantAdapter mTrendingRestAdapter;

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
    public void brownieClick(){
        startActivity(new Intent(requireActivity(),UserBrownieCashActivity.class));
    }

    @OnClick(R.id.container_home_claim_rewards)
    public void claimRewardsClick(){
        startActivity(new Intent(requireActivity(),UserClaimRewardsActivity.class));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);
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

    }

    public void enableDisableSwipeRefresh(boolean enable) {
            ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(enable);
    }
}

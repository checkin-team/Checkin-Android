package com.checkin.app.checkin.Review.ShopReview;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;
import com.checkin.app.checkin.Utility.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopReviewFragment extends Fragment implements ShopReviewAdapter.ReviewInteraction {
    private Unbinder unbinder;

    @BindView(R.id.refresh_shop_reviews)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_shop_reviews)
    RecyclerView rvShopReview;

    private ShopReviewViewModel mViewModel;
    private ShopReviewAdapter mAdapter;

    public static ShopReviewFragment newInstance() {
        return new ShopReviewFragment();
    }

    public ShopReviewFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_reviews, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new ShopReviewAdapter(this);
        rvShopReview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvShopReview.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.updateResults();
        });

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopReviewViewModel.class);

        mViewModel.getReviewData().observe(this, shopReviewModelResource -> {
            if (shopReviewModelResource == null)
                return;
            if (shopReviewModelResource.status == Resource.Status.SUCCESS && shopReviewModelResource.data != null) {
                mAdapter.updateShopReview(shopReviewModelResource.data);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null)
                Util.toast(requireContext(), resource.data.get("detail").asText());
            else if (resource.status != Resource.Status.LOADING && resource.message != null)
                Util.toast(requireContext(), resource.message);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onToggleLike(ShopReviewModel review) {
        review.toggleLike();
        mViewModel.reactToReview(review);
    }

    @Override
    public void onThumbnailClick(ShopReviewModel review, int index) {

    }

    @Override
    public void onRatingClick(ShopReviewModel review) {

    }

    @Override
    public void onFollowClick(BriefModel user) {

    }

    @Override
    public void onUserClick(BriefModel user) {
        Intent intent = new Intent(requireContext(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.KEY_PROFILE_USER_ID, user.getPk());
        startActivity(intent);
    }
}

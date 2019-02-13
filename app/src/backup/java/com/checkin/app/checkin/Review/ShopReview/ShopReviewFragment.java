package com.checkin.app.checkin.Review.ShopReview;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.ImageGallery.ImageGalleryActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;
import com.checkin.app.checkin.Utility.Utils;

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
                Utils.toast(requireContext(), resource.data.get("detail").asText());
            else if (resource.status != Resource.Status.LOADING && resource.message != null)
                Utils.toast(requireContext(), resource.message);
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
        Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
        intent.putExtra(ImageGalleryActivity.REVIEW_ID, review.getPk());
        intent.putExtra(ImageGalleryActivity.INDEX, index);
        startActivity(intent);
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

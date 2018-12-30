package com.checkin.app.checkin.Shop.ShopReview;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class ShopReviewFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.rv_shop_reviews)
    RecyclerView rvShopReview;
    private ShopReviewViewModel mViewModel;
    private ShopReviewAdapter mAdapter;


    public ShopReviewFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_review, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new ShopReviewAdapter(null);
        rvShopReview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvShopReview.setAdapter(mAdapter);
        mViewModel = ViewModelProviders.of(this).get(ShopReviewViewModel.class);
        mViewModel.getReviewData().observe(this, shopReviewModelResource -> {
            if (shopReviewModelResource == null)
                return;
            if (shopReviewModelResource.status == Resource.Status.SUCCESS && shopReviewModelResource.data != null) {
                mAdapter.updateShopReview(shopReviewModelResource.data);
            }
        });
        mViewModel.setDummyData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

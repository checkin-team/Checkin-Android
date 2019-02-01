package com.checkin.app.checkin.Shop.Ignored;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.CoverPagerAdapter;
import com.checkin.app.checkin.Misc.StatusTextAdapter;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Edit.EditProfileActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Finance.FinanceDetailActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice.ShopInvoiceListActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.LogoCoverActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.MembersActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.ShopProfileViewModel;
import com.checkin.app.checkin.Utility.Utils;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Bhavik Patel on 25/08/2018.
 */

public class ShopProfileFragment extends Fragment {
    private static final String TAG = ShopProfileFragment.class.getSimpleName();
    private Unbinder unbinder;
    public static final String KEY_SHOP_PK = "shop_private.pk";

    @BindView(R.id.tv_shop_name) TextView tvShopName;
    @BindView(R.id.tv_locality) TextView tvLocality;
    @BindView(R.id.tv_tag_line) TextView tvTagLine;
    @BindView(R.id.im_status) ImageView imStatus;
    @BindView(R.id.tv_followers) TextView tvFollowers;
    @BindView(R.id.tv_reviews) TextView tvReviews;
    @BindView(R.id.tv_checkins) TextView tvCheckins;
    @BindView(R.id.rv_additional_data) RecyclerView rvAdditionalData;
    @BindView(R.id.pager_cover) ViewPager vPagerCover;
    @BindView(R.id.indicator_top_cover) PageIndicatorView vPivCover;

    private ShopProfileViewModel mViewModel;
    private StatusTextAdapter mExtraDataAdapter;
    private CoverPagerAdapter mCoverPagerAdapter;
    private Intent mImageChangeIntent;

    public static ShopProfileFragment newInstance() {
        return new ShopProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.ignored_fragment_shop_profile_private, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopProfileViewModel.class);
        mImageChangeIntent = new Intent(requireContext(), LogoCoverActivity.class);

        mCoverPagerAdapter = new CoverPagerAdapter();
        vPagerCover.setAdapter(mCoverPagerAdapter);
        vPivCover.setViewPager(vPagerCover);
        vPivCover.setAnimationType(AnimationType.FILL);
        vPivCover.setClickListener(position -> vPagerCover.setCurrentItem(position));

        mExtraDataAdapter = new StatusTextAdapter(R.drawable.ic_check_small);
        rvAdditionalData.setAdapter(mExtraDataAdapter);
        rvAdditionalData.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));

        mViewModel.getShopData().observe(this, shopResource -> {
            if (shopResource == null) return;

            if (shopResource.status == Resource.Status.SUCCESS && shopResource.data != null) {
                this.setupData(shopResource.data);
            } else if (shopResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(ShopProfileFragment.this.getContext(), "Error fetching RestaurantModel Home! Status: " +
                        shopResource.status.toString() + "\nDetails: " + shopResource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupData(RestaurantModel shop) {
        tvShopName.setText(shop.getName());
        tvLocality.setText(shop.getLocality());
        tvTagLine.setText(shop.getTagline());
        mExtraDataAdapter.setData(shop.getExtraData());
        mCoverPagerAdapter.setData(shop.getCovers());

        tvCheckins.setText(shop.formatCheckins());
        tvFollowers.setText(shop.formatFollowers());
        tvReviews.setText(shop.formatReviews());

        if (shop.isValidStatus())
            imStatus.setVisibility(View.INVISIBLE);
        imStatus.setTag(shop.getShopStatus());

        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_LOGO, shop.getLogoUrl());
        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_COVERS, shop.getCovers());
    }

    @OnClick(R.id.im_status)
    public void onStatusClick(View v) {
        Utils.toast(requireContext(), v.getTag().toString());
    }

    @OnClick(R.id.btn_profile_edit)
    public void onEditProfile(View v) {
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        startActivity(intent);
    }

    @OnClick(R.id.btn_add_image)
    public void onAddImage(View v) {
        startActivity(mImageChangeIntent);
    }

    @OnClick({R.id.btn_members, R.id.btn_invoice, R.id.btn_insights, R.id.btn_discount})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_members:
                intent = new Intent(requireContext(), MembersActivity.class);
                intent.putExtra(MembersActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
            case R.id.btn_invoice:
                intent = new Intent(v.getContext(), ShopInvoiceListActivity.class);
                intent.putExtra(ShopInvoiceListActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
            case R.id.btn_insights:
                break;
            case R.id.btn_discount:
                intent = new Intent(v.getContext(), FinanceDetailActivity.class);
                intent.putExtra(FinanceDetailActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

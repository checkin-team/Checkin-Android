package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShopPrivateProfileFragment extends Fragment {

    @BindView(R.id.indicator_top_cover)
    PageIndicatorView indicatorTopCover;
    @BindView(R.id.btn_add_image)
    ImageView btnAddImage;
    @BindView(R.id.vp_image)
    DynamicSwipableViewPager vpImage;
    @BindView(R.id.ll_image_swipe)
    LinearLayout llImageSwipe;
    @BindView(R.id.im_status)
    ImageView imStatus;
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    @BindView(R.id.tv_shop_address)
    TextView tvShopAddress;
    @BindView(R.id.btn_profile_edit)
    Button btnProfileEdit;
    @BindView(R.id.btn_members)
    LinearLayout btnMembers;
    @BindView(R.id.btn_invoice)
    LinearLayout btnInvoice;
    @BindView(R.id.btn_discount)
    LinearLayout btnDiscount;
    @BindView(R.id.tv_checkins)
    TextView tvCheckins;
    @BindView(R.id.rv_additional_data)
    RecyclerView rvAdditionalData;
    private Unbinder unbinder;

    public static final String KEY_SHOP_PK = "shop_private.pk";

    private ShopProfileViewModel mViewModel;
    private CoverPagerAdapter mCoverPagerAdapter;
    private Intent mImageChangeIntent;
    private StatusTextAdapter mExtraDataAdapter;

    public ShopPrivateProfileFragment() {
    }

    public static Fragment newInstance() {
        return new ShopPrivateProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_private_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopProfileViewModel.class);
        mImageChangeIntent = new Intent(requireContext(), LogoCoverActivity.class);

        mCoverPagerAdapter = new CoverPagerAdapter();
        vpImage.setAdapter(mCoverPagerAdapter);
        indicatorTopCover.setViewPager(vpImage);
        indicatorTopCover.setAnimationType(AnimationType.FILL);
        indicatorTopCover.setClickListener(position -> vpImage.setCurrentItem(position));

        mExtraDataAdapter = new StatusTextAdapter(R.drawable.ic_oval_red_white_tick);
        rvAdditionalData.setAdapter(mExtraDataAdapter);
        rvAdditionalData.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));

        mViewModel.getShopData().observe(this, shopResource -> {
            if (shopResource == null) return;

            if (shopResource.status == Resource.Status.SUCCESS && shopResource.data != null) {
                this.setupData(shopResource.data);
            } else if (shopResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(ShopPrivateProfileFragment.this.getContext(), "Error fetching RestaurantModel Home! Status: " + shopResource.status.toString() + "\nDetails: " + shopResource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupData(RestaurantModel shop) {
        tvShopName.setText(shop.getName());
        mCoverPagerAdapter.setData(shop.getCovers());
        mExtraDataAdapter.setData(shop.getExtraData());
        tvCheckins.setText(shop.formatCheckins());

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

    @OnClick({R.id.btn_members, R.id.btn_invoice, R.id.btn_discount})
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
            case R.id.btn_discount:
                intent = new Intent(v.getContext(), FinanceDetailActivity.class);
                intent.putExtra(FinanceDetailActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
        }
    }
}

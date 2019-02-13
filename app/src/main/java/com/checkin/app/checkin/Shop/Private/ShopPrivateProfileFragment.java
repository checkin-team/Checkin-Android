package com.checkin.app.checkin.Shop.Private;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.CoverPagerAdapter;
import com.checkin.app.checkin.Misc.StatusTextAdapter;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Edit.EditProfileActivity;
import com.checkin.app.checkin.Shop.Private.Finance.FinanceDetailActivity;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopInvoiceListActivity;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShopPrivateProfileFragment extends Fragment {
    private Unbinder unbinder;

    public static final String KEY_SHOP_PK = "shop_private.pk";

    @BindView(R.id.indicator_shop_private_top_cover)
    PageIndicatorView indicatorTopCover;
    @BindView(R.id.btn_shop_private_add_logo)
    ImageView btnAddImage;
    @BindView(R.id.vp_shop_private_cover)
    DynamicSwipableViewPager vpImage;
    @BindView(R.id.im_shop_private_status)
    ImageView imStatus;
    @BindView(R.id.tv_shop_private_checkins)
    TextView tvCheckins;
    @BindView(R.id.tv_shop_private_locality)
    TextView tvLocality;
    @BindView(R.id.tv_shop_private_display_name)
    TextView tvShopName;
    @BindView(R.id.rv_shop_private_extra_data)
    RecyclerView rvAdditionalData;

    private ShopProfileViewModel mViewModel;
    private CoverPagerAdapter mCoverPagerAdapter;
    private StatusTextAdapter mExtraDataAdapter;
    private Intent mImageChangeIntent;

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

        mImageChangeIntent = new Intent(getContext(), LogoCoverActivity.class);
        mCoverPagerAdapter = new CoverPagerAdapter();
        vpImage.setAdapter(mCoverPagerAdapter);
        indicatorTopCover.setViewPager(vpImage);
        indicatorTopCover.setAnimationType(AnimationType.FILL);
        indicatorTopCover.setClickListener(position -> vpImage.setCurrentItem(position));

        mExtraDataAdapter = new StatusTextAdapter(R.drawable.ic_oval_red_white_tick);
        rvAdditionalData.setAdapter(mExtraDataAdapter);
        rvAdditionalData.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));

        mViewModel.getShopData().observe(this, shopResource -> {
            if (shopResource == null) return;

            if (shopResource.status == Resource.Status.SUCCESS && shopResource.data != null) {
                this.setupData(shopResource.data);
            } else if (shopResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Utils.toast(requireContext(), "Error!\n" + shopResource.message);
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
        tvLocality.setText(shop.getLocality());

        if (shop.isValidStatus())
            imStatus.setVisibility(View.INVISIBLE);
        imStatus.setTag(shop.getShopStatus());

        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_LOGO, shop.getLogoUrl());
        mImageChangeIntent.putExtra(LogoCoverActivity.KEY_SHOP_COVERS, shop.getCovers());
    }

    @OnClick(R.id.im_shop_private_status)
    public void onStatusClick(View v) {
        Utils.toast(requireContext(), v.getTag().toString());
    }

    @OnClick(R.id.btn_shop_private_edit)
    public void onEditProfile(View v) {
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.KEY_SHOP_PK, mViewModel.getShopPk());
        startActivity(intent);
    }

    @OnClick(R.id.btn_shop_private_add_logo)
    public void onAddImage(View v) {
        startActivity(mImageChangeIntent);
    }

    @OnClick({R.id.btn_shop_private_members, R.id.btn_shop_private_invoice, R.id.btn_shop_private_discount})
    public void onClickAction(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_shop_private_members:
                intent = new Intent(requireContext(), MembersActivity.class);
                intent.putExtra(MembersActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
            case R.id.btn_shop_private_invoice:
                intent = new Intent(v.getContext(), ShopInvoiceListActivity.class);
                intent.putExtra(ShopInvoiceListActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
            case R.id.btn_shop_private_discount:
                intent = new Intent(v.getContext(), FinanceDetailActivity.class);
                intent.putExtra(FinanceDetailActivity.KEY_SHOP_PK, mViewModel.getShopPk());
                startActivity(intent);
                break;
        }
    }
}

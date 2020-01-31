package com.checkin.app.checkin.Shop.Private.Insight;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter;
import com.checkin.app.checkin.utility.Utils;

import java.util.Locale;

import butterknife.BindView;

public class ShopInsightLoyaltyProgramFragment extends BaseFragment {
    @BindView(R.id.tv_shop_insight_loyalty_total_discount)
    TextView tvDiscount;
    @BindView(R.id.rv_shop_insight_loyalty)
    RecyclerView rvLoyalty;

    private ActiveSessionPromoAdapter mAdapter;
    private ShopInsightViewModel mViewModel;

    public ShopInsightLoyaltyProgramFragment() {
    }

    public static ShopInsightLoyaltyProgramFragment newInstance() {
        return new ShopInsightLoyaltyProgramFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_shop_insight_loyalty_program;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi();
        getData();
    }

    private void setUpUi() {
        mAdapter = new ActiveSessionPromoAdapter(null, null);
        rvLoyalty.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvLoyalty.setAdapter(mAdapter);
    }

    private void getData() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopInsightViewModel.class);

        mViewModel.getShopActivePromos().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null)
                mAdapter.setData(listResource.getData());
            else if (listResource.getStatus() != Resource.Status.LOADING)
                Utils.toast(requireContext(), listResource.getMessage());
        });

        mViewModel.getInsightLoyaltyDetail().observe(this, shopInsightLoyaltyProgramModelResource -> {
            if (shopInsightLoyaltyProgramModelResource == null)
                return;
            if (shopInsightLoyaltyProgramModelResource.getStatus() == Resource.Status.SUCCESS && shopInsightLoyaltyProgramModelResource.getData() != null)
                tvDiscount.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(tvDiscount.getContext()), shopInsightLoyaltyProgramModelResource.getData().getDiscounts()));
        });
    }
}

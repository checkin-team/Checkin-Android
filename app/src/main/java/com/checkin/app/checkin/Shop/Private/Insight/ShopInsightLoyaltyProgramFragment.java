package com.checkin.app.checkin.Shop.Private.Insight;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.ActiveSessionInvoiceViewModel;
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter;

import java.util.Locale;

import butterknife.BindView;

public class ShopInsightLoyaltyProgramFragment extends BaseFragment {
    @BindView(R.id.tv_shop_insight_loyalty_total_discount)
    TextView tvDiscount;
    @BindView(R.id.rv_shop_insight_loyalty)
    RecyclerView rvLoyalty;

    private ActiveSessionPromoAdapter mAdapter;
    private ShopInsightViewModel mViewModel;
    private ActiveSessionInvoiceViewModel mInvoiceViewModel;

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
        mInvoiceViewModel = ViewModelProviders.of(requireActivity()).get(ActiveSessionInvoiceViewModel.class);
        mInvoiceViewModel.fetchAvailablePromoCodes();
        mViewModel.fetchShopInsightLoyaltyDetail();

        mInvoiceViewModel.getPromoCodes().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                mAdapter.setData(listResource.data);
            else if (listResource.status != Resource.Status.LOADING)
                Utils.toast(requireContext(), listResource.message);
        });

        mViewModel.getInsightLoyaltyDetail().observe(this, shopInsightLoyaltyProgramModelResource -> {
            if (shopInsightLoyaltyProgramModelResource == null)
                return;
            if (shopInsightLoyaltyProgramModelResource.status == Resource.Status.SUCCESS && shopInsightLoyaltyProgramModelResource.data != null)
                tvDiscount.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(tvDiscount.getContext()), shopInsightLoyaltyProgramModelResource.data.getDiscounts()));
        });
    }
}

package com.checkin.app.checkin.Shop.Private.Insight;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.util.Locale;

import androidx.annotation.Nullable;

import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Inventory.InventoryActivity.KEY_INVENTORY_RESTAURANT_PK;

public class ShopInsightActivity extends BaseActivity {

    @BindView(R.id.tv_shop_insight_total_sale)
    TextView tvTotalSale;
    @BindView(R.id.tv_shop_insight_average_session_time)
    TextView tvSessionTime;
    @BindView(R.id.tv_shop_insight_average_serving_time)
    TextView tvServingTime;
    @BindView(R.id.card_shop_insight_revenue)
    ViewGroup cardRevenue;
    @BindView(R.id.card_shop_insight_loyalty_program)
    ViewGroup cardLoyaltyProgram;
    @BindView(R.id.card_shop_insight_loyalty_program_selected)
    ViewGroup cardLoyaltyProgramSelected;
    @BindView(R.id.card_shop_insight_revenue_selected)
    ViewGroup cardRevenueSelected;
    private ShopInsightRevenueFragment mRevenueFragment;
    private ShopInsightLoyaltyProgramFragment mLoyaltyProgramFragment;
    private ShopInsightViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insight);
        ButterKnife.bind(this);

        setUpUi();
        setData();
    }

    private void setUpUi() {
        mRevenueFragment = ShopInsightRevenueFragment.newInstance();
        mLoyaltyProgramFragment = ShopInsightLoyaltyProgramFragment.newInstance();
        onRevenueClick();
    }

    private void setData(){
        mViewModel = ViewModelProviders.of(this).get(ShopInsightViewModel.class);
        long restaurantPk = getIntent().getLongExtra(KEY_INVENTORY_RESTAURANT_PK, 0);
        mViewModel.fetchShopInsightRevenueDetail(restaurantPk);
        mViewModel.getInsightRevenueDetail().observe(this, shopInsightRevenueModelResource -> {
            if (shopInsightRevenueModelResource == null)
                return;
            if (shopInsightRevenueModelResource.status == Resource.Status.SUCCESS && shopInsightRevenueModelResource.data != null) {
                ShopInsightRevenueModel revenueModel =  shopInsightRevenueModelResource.data;
                tvTotalSale.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(tvTotalSale.getContext()), revenueModel.getSales()));
                tvSessionTime.setText(revenueModel.formatAvgSessionTime());
                tvServingTime.setText(revenueModel.formatAvgServingTime());
            }
        });
    }

    @OnClick(R.id.card_shop_insight_revenue)
    public void onRevenueClick(){
        showRevenueSelected();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_shop_insight_details, mRevenueFragment)
                .commit();
    }
    @OnClick(R.id.card_shop_insight_loyalty_program)
    public void onLoyaltyClick(){
        showLoyaltySelected();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_shop_insight_details, mLoyaltyProgramFragment)
                .commit();
    }

    public void showRevenueSelected(){
        cardRevenue.setVisibility(View.GONE);
        cardRevenueSelected.setVisibility(View.VISIBLE);
        cardLoyaltyProgram.setVisibility(View.VISIBLE);
        cardLoyaltyProgramSelected.setVisibility(View.GONE);
    }

    public void showLoyaltySelected(){
        cardRevenue.setVisibility(View.VISIBLE);
        cardRevenueSelected.setVisibility(View.GONE);
        cardLoyaltyProgram.setVisibility(View.GONE);
        cardLoyaltyProgramSelected.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.container_shop_insight_top)
    public void onBackClicked() {
        onBackPressed();
    }
}

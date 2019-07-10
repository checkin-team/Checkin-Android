package com.checkin.app.checkin.Shop.ShopJoin;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Edit.EditAspectFragment;
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;
import com.checkin.app.checkin.Shop.RestaurantModel;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopJoinActivity extends BaseActivity implements
        BasicInfoFragment.BasicInfoFragmentInteraction, EditAspectFragment.AspectFragmentInteraction {
    public static final String KEY_SHOP_EMAIL = "shop_email";
    public static final String KEY_SHOP_PHONE_TOKEN = "shop_phone";
    private static final String TAG = ShopJoinActivity.class.getSimpleName();
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private JoinViewModel mJoinViewModel;
    private ShopProfileViewModel mShopViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_join);
        ButterKnife.bind(this);

        initProgressBar(R.id.pb_shop_join);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        mJoinViewModel = ViewModelProviders.of(this).get(JoinViewModel.class);
        mShopViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);

        String email = getIntent().getStringExtra(KEY_SHOP_EMAIL);
        String phoneToken = getIntent().getStringExtra(KEY_SHOP_PHONE_TOKEN);

        mJoinViewModel.newShopJoin(email, phoneToken);

        mShopViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                long pk = resource.getData().get("pk").longValue();
                finishSignup(pk);
            } else {
                mShopViewModel.showError(resource.getErrorBody());
            }
        });

        mJoinViewModel.getJoinResults().observe(this, genericDetailModelResource -> {
            if (genericDetailModelResource == null)
                return;
            if (genericDetailModelResource.getStatus() == Resource.Status.SUCCESS && genericDetailModelResource.getData() != null) {
                hideProgressBar();
                btnNext.setActivated(true);
            } else if (genericDetailModelResource.getStatus() == Resource.Status.LOADING) {
                visibleProgressBar();
            } else {
                hideProgressBar();
                btnNext.setActivated(true);
            }
        });

        askBasicInfo();
    }

    private void finishSignup(long pk) {
        Intent intent = Intent.makeRestartActivityTask(new ComponentName(this, ShopPrivateActivity.class));
        intent.putExtra(ShopPrivateActivity.KEY_SHOP_PK, pk);
        startActivity(intent);
    }

    private void askBasicInfo() {
        btnNext.setActivated(false);
        tvTitle.setText(R.string.title_shop_basic_info);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, BasicInfoFragment.newInstance(this))
                .commit();
    }

    private void askAspectInfo() {
        btnNext.setActivated(false);
        tvTitle.setText(R.string.title_shop_aspects);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EditAspectFragment.newInstance(this))
                .commit();
    }

    @OnClick(R.id.btn_next)
    public void onNextClick(View view) {
        if (!view.isActivated())
            return;
        if (mJoinViewModel.isRegistered()) {
            mShopViewModel.collectData();
        } else {
            mJoinViewModel.registerNewBusiness();
            btnNext.setActivated(false);
        }

    }

    @Override
    public void onShopRegistered(GenericDetailModel details) {
        RestaurantModel shop = mJoinViewModel.getNewShop(details.getPk());
        mShopViewModel.useShop(shop);
        askAspectInfo();
    }

    @Override
    public void onBasicDataValidStatus(boolean isValid) {
        btnNext.setActivated(isValid);
    }

    @Override
    public void updateShopAspects(RestaurantModel shop) {
        mShopViewModel.updateShop(shop);
    }

    @Override
    public void onAspectDataValidStatus(boolean isValid) {
        btnNext.setActivated(isValid);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package com.checkin.app.checkin.Shop.ShopJoin;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.Private.Edit.EditAspectFragment;
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopJoinActivity extends AppCompatActivity implements
        BasicInfoFragment.BasicInfoFragmentInteraction, EditAspectFragment.AspectFragmentInteraction {
    private static final String TAG = ShopJoinActivity.class.getSimpleName();
    public static final String KEY_SHOP_EMAIL = "shop_email";
    public static final String KEY_SHOP_PHONE_TOKEN = "shop_phone";

    @BindView(R.id.btn_next) Button btnNext;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.pb_shop_join)
    ProgressBar pbShopJoin;

    private JoinViewModel mJoinViewModel;
    private ShopProfileViewModel mShopViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_join);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }
        pbShopJoin.setVisibility(View.GONE);
        mJoinViewModel = ViewModelProviders.of(this).get(JoinViewModel.class);
        mShopViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);

        String email = getIntent().getStringExtra(KEY_SHOP_EMAIL);
        String phoneToken = getIntent().getStringExtra(KEY_SHOP_PHONE_TOKEN);

        mJoinViewModel.newShopJoin(email, phoneToken);

        mShopViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                long pk = resource.data.get("pk").longValue();
                finishSignup(pk);
            } else {
                mShopViewModel.showError(resource.getErrorBody());
            }
        });

        mJoinViewModel.getJoinResults().observe(this, genericDetailModelResource -> {
            if (genericDetailModelResource == null)
                return;
            if (genericDetailModelResource.status == Resource.Status.SUCCESS && genericDetailModelResource.data != null) {
                pbShopJoin.setVisibility(View.GONE);
                btnNext.setActivated(true);
            } else if (genericDetailModelResource.status == Resource.Status.LOADING)  {
                pbShopJoin.setVisibility(View.VISIBLE);
            }  else {
                pbShopJoin.setVisibility(View.GONE);
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
        }
        else{
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

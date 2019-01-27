package com.checkin.app.checkin.Shop.ShopJoin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.Edit.EditAspectFragment;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.ShopActivity;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.ShopProfileViewModel;

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

    private JoinViewModel mJoinViewModel;
    private ShopProfileViewModel mShopViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_join);
        ButterKnife.bind(this);

        mJoinViewModel = ViewModelProviders.of(this).get(JoinViewModel.class);
        mShopViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);

        String email = getIntent().getStringExtra(KEY_SHOP_EMAIL);
        String phoneToken = getIntent().getStringExtra(KEY_SHOP_PHONE_TOKEN);

        mJoinViewModel.newShopJoin(email, phoneToken);

        mShopViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                String pk = resource.data.get("pk").textValue();
                finishSignup(pk);
            } else {
                mShopViewModel.showError(resource.getErrorBody());
            }
        });

        askBasicInfo();
    }

    private void finishSignup(String pk) {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(ShopActivity.KEY_SHOP_PK, pk);
        startActivity(intent);
        finish();
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
        else
            mJoinViewModel.registerNewBusiness();
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

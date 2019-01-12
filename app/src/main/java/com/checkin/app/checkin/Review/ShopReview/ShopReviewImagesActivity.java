package com.checkin.app.checkin.Review.ShopReview;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;

import butterknife.ButterKnife;

public class ShopReviewImagesActivity extends BaseActivity {

    private ShopReviewViewModel mViewModel;
    public static final String KEY_SHOP_PK = "shop.reviews";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_review_images_thumbnail);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_appbar_back);
            getSupportActionBar().setElevation(10);
        }


        String shopPk = getIntent().getStringExtra(KEY_SHOP_PK);
        mViewModel.fetchShopReviews(shopPk);
    }


}

package com.checkin.app.checkin.Shop.Private;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogoCoverActivity extends AppCompatActivity implements ShopCoverAdapter.ShopCoverInteraction {
    private static final String TAG = LogoCoverActivity.class.getSimpleName();

    public static final String KEY_SHOP_PK = "shop.pk";
    public static final String KEY_SHOP_COVERS = "shop.covers";
    public static final String KEY_SHOP_LOGO = "shop.logo";

    private static final int RC_COVER_BASE = 100;
    private static final int RC_LOGO = 50;

    @BindView(R.id.im_sc_logo)
    ImageView imLogo;
    @BindView(R.id.rv_sc_covers)
    RecyclerView rvCovers;

    private ShopProfileViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop_logo_cover);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setElevation(10);
        }

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        setupUi();

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Utils.toast(this, resource.data.get("detail").asText());
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(this, resource.message);
            }
        });
    }

    private void setupUi() {
        String[] coverUrls = getIntent().getStringArrayExtra(KEY_SHOP_COVERS);
        String logoUrl = getIntent().getStringExtra(KEY_SHOP_LOGO);
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);

        Log.e(TAG, "SHOP: " + shopPk + logoUrl + Arrays.toString(coverUrls));

        mViewModel.setShopPk(shopPk);
        ShopCoverAdapter adapter = new ShopCoverAdapter(coverUrls, this);
        rvCovers.setAdapter(adapter);
        rvCovers.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        Utils.loadImageOrDefault(imLogo, logoUrl, R.drawable.card_image_add);
    }

    @Override
    public void onCoverRemove(int index) {
        mViewModel.removeCoverImage(index);
    }

    @OnClick(R.id.im_sc_logo)
    public void onLogoClick() {
        Intent intent = new Intent(this, SelectCropImageActivity.class);
        startActivityForResult(intent, RC_LOGO);
    }

    @Override
    public void onCoverChange(int index) {
        Intent intent = new Intent(this, SelectCropImageActivity.class);
        startActivityForResult(intent, RC_COVER_BASE + index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                File image = (File) data.getExtras().get(SelectCropImageActivity.KEY_IMAGE);
                if (requestCode == RC_LOGO) {
                    mViewModel.updateCoverPic(image, this, -1);
                } else {
                    mViewModel.updateCoverPic(image, this, requestCode - RC_COVER_BASE);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

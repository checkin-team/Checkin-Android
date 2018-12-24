package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.Utility.GlideApp;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShopLogoEditActivity extends AppCompatActivity {


    @BindView(R.id.change_logo) CircleImageView change_logo;
    @BindView(R.id.rvShopCover) RecyclerView rvShopCover;
    
    private ShopProfileViewModel mViewModel;
    RestaurantModel shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_personal_profile_edit);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this, new ShopProfileViewModel.Factory(getApplication())).get(ShopProfileViewModel.class);


        mViewModel.getShopData().observe( this, shopResource -> {
            if (shopResource == null) return;

            if (shopResource.status == Resource.Status.SUCCESS && shopResource.data != null) {
                this.setUI(shopResource.data);
            } else if (shopResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(getApplicationContext(), "Error fetching RestaurantModel Home! Status: " +
                        shopResource.status.toString() + "\nDetails: " + shopResource.message, Toast.LENGTH_LONG).show();
            }
        });

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(ShopLogoEditActivity.this, 2);
        rvShopCover.setLayoutManager(mGridLayoutManager);

        }

    private void setUI(RestaurantModel shop){

        if(shop.getLogoUrl() != null){

            GlideApp.with(this).load(shop.getLogoUrl()).into(change_logo);
            Log.i("image set", "logo done");
        }
    }


    @OnClick(R.id.change_logo)
    public void editProfilePic() {
        Intent intent;
        intent = new Intent(this , SelectCropImageActivity.class);
        startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectCropImageActivity.RC_CROP_IMAGE && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                File image = (File) data.getExtras().get(SelectCropImageActivity.KEY_IMAGE);
                mViewModel.postShopLogo(image);
            }
        }
    }


}


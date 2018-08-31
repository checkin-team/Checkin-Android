package com.checkin.app.checkin.Profile.ShopProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopProfile extends AppCompatActivity {

    @BindView(R.id.imagePager)  ViewPager imagePager;
    @BindView(R.id.hotel)  TextView hotel;
    @BindView(R.id.followers)  TextView followers;
    @BindView(R.id.checkIn)  TextView checkIn;
    @BindView(R.id.rating)  TextView rating;
    private ShopProfileViewModel shopProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile2);
        ButterKnife.bind(this);
        imagePager.setAdapter(new ImagePager());
        shopProfileViewModel = ViewModelProviders.of(this, new ShopProfileViewModel.Factory(getApplication())).get(ShopProfileViewModel.class);
        shopProfileViewModel.getShopHomeModel(1).observe(this, shopHomeModel -> {
            if (shopHomeModel == null) return;
            if (shopHomeModel.status == Resource.Status.SUCCESS) {
                List<ShopHomeModel> shopHomeModelList = shopHomeModel.data;
                if (shopHomeModelList.size() > 0) {
                    hotel.setText(shopHomeModelList.get(0).getName());
                }
                //TODO complete ordered items
            } else if (shopHomeModel.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(getApplicationContext(), "Error fetching Shop Home! Status: " +
                        shopHomeModel.status.toString() + "\nDetails: " + shopHomeModel.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class ImagePager extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(collection.getContext());
            ImageView imageView = new ImageView(collection.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(R.drawable.dummy_shop);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            collection.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}

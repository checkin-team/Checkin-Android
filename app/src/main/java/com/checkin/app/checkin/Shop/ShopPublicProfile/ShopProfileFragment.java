package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Shop.ShopReviewsActivity;

/**
 * Created by Bhavik Patel on 17/08/2018.
 */

public class ShopProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ShopProfileFragment.class.getSimpleName();

    private  ViewPager imagePager;
    private RecyclerView grid;
    private View call;
    private View review;
    private View navigate;
    private View follow;
    private  TextView hotel;
    private TextView reviews;


    private int targetId;

    private ShopProfileViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_profile_public,container,false);
        imagePager = view.findViewById(R.id.imagePager);
        grid = view.findViewById(R.id.grid);
        follow = view.findViewById(R.id.follow);
        call = view.findViewById(R.id.call);
        review = view.findViewById(R.id.review);
        navigate = view.findViewById(R.id.navigation);
        hotel = view.findViewById(R.id.hotel);
        setUp();

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShopReviewsActivity.class));
            }
        });

        mViewModel = ViewModelProviders.of(this, new ShopProfileViewModel.Factory(getActivity().getApplication())).get(ShopProfileViewModel.class);
        mViewModel.getShopModel(targetId).observe(this, shopModelResource -> {
            if (shopModelResource == null) return;
            if (shopModelResource.status == Resource.Status.SUCCESS && shopModelResource.data != null) {
                ShopModel shop = shopModelResource.data;
            } else if (shopModelResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(ShopProfileFragment.this.getContext(), "Error fetching Shop Home! Status: " +
                        shopModelResource.status.toString() + "\nDetails: " + shopModelResource.message, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public static ShopProfileFragment getInstance(int targetId){
        ShopProfileFragment fragment = new ShopProfileFragment();
        fragment.targetId = targetId;
        return fragment;
    }

    private void setUp(){
        imagePager.setAdapter(new ImagePager());
        grid.setAdapter(new AboutPager());
        grid.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false));

        follow.setOnClickListener(this);
        call.setOnClickListener(this);
        review.setOnClickListener(this);
        navigate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.follow:
                break;
            case R.id.call:
                Toast.makeText(getContext(),"Call",Toast.LENGTH_SHORT).show();
                mViewModel.callShop(getContext());
                break;
            case R.id.navigation:
                break;
            case R.id.review:
                break;
        }
    }

    private class ImagePager extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(collection.getContext());
            ImageView imageView = new ImageView(collection.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(R.drawable.restaurant_profile);
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

    private class AboutPager extends RecyclerView.Adapter<AboutPager.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView text;
            public ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_shop_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.text.setText("Info at position " + position);
        }

        @Override
        public int getItemCount() {
            return 11;
        }
    }
}

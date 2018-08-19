package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
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

import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhavik Patel on 17/08/2018.
 */

public class ShopHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ShopHomeFragment.class.getSimpleName();

    private  ViewPager imagePager;
    private RecyclerView grid;
    private View call;
    private View message;
    private View navigate;
    private View follow;
    private  TextView hotel;
    private String mobNumber = "+9717477205";
    private ShopHomeViewModel mShopHomeViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shop_profile_home,container,false);
        imagePager = view.findViewById(R.id.imagePager);
        grid = view.findViewById(R.id.grid);
        follow = view.findViewById(R.id.follow);
        call = view.findViewById(R.id.call);
        message = view.findViewById(R.id.message);
        navigate = view.findViewById(R.id.navigation);
        hotel = view.findViewById(R.id.hotel);
        setUp();
        mShopHomeViewModel = ViewModelProviders.of(this, new ShopHomeViewModel.Factory(getActivity().getApplication())).get(ShopHomeViewModel.class);
        mShopHomeViewModel.getShopHomeModel(1).observe(this, shopHomeModel -> {
            if(shopHomeModel == null) return;
            if (shopHomeModel.status == Resource.Status.SUCCESS) {
                List<ShopHomeModel> shopHomeModelList = shopHomeModel.data;
                if(shopHomeModelList.size()>0){
                    hotel.setText(shopHomeModelList.get(0).getName());
                }
                //TODO complete ordered items
            } else if (shopHomeModel.status == Resource.Status.LOADING) {
                // LOADING
            } else{
                Toast.makeText(getContext(), "Error fetching Shop Home! Status: " +
                        shopHomeModel.status.toString() + "\nDetails: " + shopHomeModel.message, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void setUp(){
        imagePager.setAdapter(new ImagePager());
        grid.setAdapter(new AboutPager());
        grid.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false));

        follow.setOnClickListener(this);
        call.setOnClickListener(this);
        message.setOnClickListener(this);
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
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mobNumber));
                startActivity(intent);
                break;
            case R.id.navigation:
                break;
            case R.id.message:
                break;
        }
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

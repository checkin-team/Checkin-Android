package com.checkin.app.checkin.Shop.ShopPrivateProfile;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavik Patel on 25/08/2018.
 */

public class ShopProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ShopProfileFragment.class.getSimpleName();

    private ViewPager imagePager;
    private RecyclerView grid;
    private View members;
    private View message;
    private View insights;
    private View notification;
    private TextView hotel;
    private ShopProfileViewModel mViewModel;
    private TextView editShopProfile;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_profile_private,container,false);
        imagePager = view.findViewById(R.id.imagePager);
        grid = view.findViewById(R.id.grid);
        members = view.findViewById(R.id.members);
        insights = view.findViewById(R.id.insights);
        message = view.findViewById(R.id.review);
        notification = view.findViewById(R.id.notification);
        hotel = view.findViewById(R.id.hotel);
        editShopProfile=view.findViewById(R.id.edit_shop_profile);
        setUp();

        mViewModel = ViewModelProviders.of(this).get(ShopProfileViewModel.class);
        mViewModel.getShopData().observe(this, shopResource -> {
            if (shopResource == null) return;
            if (shopResource.status == Resource.Status.SUCCESS && shopResource.data != null) {
                ShopModel shop = shopResource.data;
            } else if (shopResource.status == Resource.Status.LOADING) {
                // LOADING
            } else {
                Toast.makeText(ShopProfileFragment.this.getContext(), "Error fetching ShopModel Home! Status: " +
                        shopResource.status.toString() + "\nDetails: " + shopResource.message, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void setUp(){
        imagePager.setAdapter(new ImagePager());
        grid.setAdapter(new AboutPager());
        grid.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false));

        insights.setOnClickListener(this);
        notification.setOnClickListener(this);
        message.setOnClickListener(this);
        members.setOnClickListener(this);

        editShopProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),EditShopProfileActivity.class));
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.members:
                break;
            case R.id.notification:
                break;
            case R.id.insights:
                break;
            case R.id.review:
                break;
        }
    }

    public static Fragment getInstance(int targetId) {
        return new ShopProfileFragment();
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
    private class AboutPager extends RecyclerView.Adapter{

        private static final int TYPE_INFO = 0;
        private static final int TYPE_ADD = 1;

        private List<String> aboutInfoList;

        public AboutPager() {
            aboutInfoList = getAboutInfo();
        }

        public class InfoViewHolder extends RecyclerView.ViewHolder{
            TextView text;
            public InfoViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
            }
            public void bind(String info){
                text.setText(info);
            }
        }
        public class AddInfoViewHolder extends RecyclerView.ViewHolder{
            View container;
            public AddInfoViewHolder(View itemView) {
                super(itemView);
                container = itemView;
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo
                        Toast.makeText(getContext(),"ADD MORE",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }



        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == TYPE_INFO){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_shop_item,parent,false);
                return new InfoViewHolder(view);
            }else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_info_shop_item,parent,false);
                return new AddInfoViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == TYPE_INFO) ((InfoViewHolder) holder).bind(aboutInfoList.get(position));
        }

        @Override
        public int getItemCount() {
            return aboutInfoList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == aboutInfoList.size()) return TYPE_ADD;
            else return TYPE_INFO;
        }

        private List<String> getAboutInfo(){
            List<String> list = new ArrayList<>();
            for(int i = 0;i < 11;i++){
                list.add("Info at position " + i);
            }
            return list;
        }
    }
}

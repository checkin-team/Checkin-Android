package com.checkin.app.checkin.RestaurantImage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageGalleryPagerAdapter extends PagerAdapter {
    private List<String> mData;
    private List<ImageView> mViews;
    private LayoutInflater inflater;

    public ImageGalleryPagerAdapter(Context context,String... data) {
        mData = Arrays.asList(data);
        mViews = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setData(String... data) {
        mData = Arrays.asList(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View imageLayout = inflater.inflate(R.layout.child_image_gallery, container, false);
        assert imageLayout != null;
        final ImageView ivGallery = (ImageView) imageLayout.findViewById(R.id.iv_gallery);
        String uri = mData.get(position);
        if (uri != null) {
            GlideApp.with(container).load(uri).into(ivGallery);
        }
        container.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((View) object));
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}

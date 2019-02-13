package com.checkin.app.checkin.Misc;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoverPagerAdapter extends PagerAdapter {
    private List<String> mData;
    private List<ImageView> mViews;

    public CoverPagerAdapter(String... data) {
        mData = Arrays.asList(data);
        mViews = new ArrayList<>();
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
        ImageView view;

        if (mViews.size() <= position) {
            view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViews.add(view);
        } else {
            view = mViews.get(position);
        }
        String uri = mData.get(position);
        if (uri != null) {
            GlideApp.with(container).load(uri).into(view);
        } else {
            view.setImageResource(R.drawable.cover_restaurant_unknown);
        }
        container.addView(view);
        return view;
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

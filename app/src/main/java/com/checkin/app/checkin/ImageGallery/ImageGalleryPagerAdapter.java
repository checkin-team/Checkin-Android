package com.checkin.app.checkin.ImageGallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageGalleryPagerAdapter extends PagerAdapter {
    private List<String> mData;
    private List<ImageView> mViews;
    private LayoutInflater inflater;
    private ProgressBar progressBar;

    public ImageGalleryPagerAdapter(Context context, ProgressBar progressBar, String... data) {
        mData = Arrays.asList(data);
        mViews = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.progressBar = progressBar;
    }

    public void setData(String... data) {
        mData = Arrays.asList(data);
        notifyDataSetChanged();
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View imageLayout = inflater.inflate(R.layout.item_image_gallery, container, false);
        assert imageLayout != null;
        final ImageView ivGallery = imageLayout.findViewById(R.id.iv_gallery_image);
        String uri = mData.get(position);
        if (uri != null) {
            GlideApp.with(container).load(uri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivGallery);
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

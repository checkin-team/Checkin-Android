package com.checkin.app.checkin.misc;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageThumbnailHolder {
    @BindView(R.id.im_thumbnail_1)
    ImageView imThumbnail1;
    @BindView(R.id.im_thumbnail_2)
    ImageView imThumbnail2;
    @BindView(R.id.im_thumbnail_3)
    FrameLayout imThumbnail3;
    @BindView(R.id.tv_thumbnail_count)
    TextView tvThumbnailsCount;
    @BindView(R.id.container_thumbnail_3)
    ViewGroup containerThumbnail3;

    public ImageThumbnailHolder(View itemView, final ImageThumbnailInteraction listener) {
        ButterKnife.bind(this, itemView);

        imThumbnail1.setOnClickListener(v -> listener.onThumbnailClick(0));
        imThumbnail2.setOnClickListener(v -> listener.onThumbnailClick(1));
        imThumbnail3.setOnClickListener(v -> listener.onThumbnailClick(2));
    }

    public void bindThumbnails(String... thumbnailUrls) {
        if (thumbnailUrls.length > 2) {
            tvThumbnailsCount.setText(String.valueOf(thumbnailUrls.length - 2));
            GlideApp.with(imThumbnail3.getContext())
                    .load(thumbnailUrls[2])
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imThumbnail3.setBackground(resource);
                        }
                    });
        } else {
            containerThumbnail3.setVisibility(View.GONE);
        }
        if (thumbnailUrls.length > 1) {
            GlideApp.with(imThumbnail2.getContext())
                    .load(thumbnailUrls[1])
                    .into(imThumbnail2);
        } else {
            imThumbnail2.setVisibility(View.GONE);
        }
        if (thumbnailUrls.length > 0) {
            GlideApp.with(imThumbnail1.getContext())
                    .load(thumbnailUrls[0])
                    .into(imThumbnail1);
        } else {
            imThumbnail1.setVisibility(View.GONE);
        }
    }

    public interface ImageThumbnailInteraction {
        void onThumbnailClick(int index);
    }
}

package com.checkin.app.checkin.ImageGallery;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageGalleryActivity extends AppCompatActivity {
    @BindView(R.id.iv_gallery_back)
    ImageView ivBack;
    @BindView(R.id.tv_gallery_uploader)
    TextView tvUploadUserName;
    @BindView(R.id.vp_gallery_pager_cover)
    ViewPager vpImagePagerCover;
    @BindView(R.id.indicator_gallery_bottom)
    PageIndicatorView indicatorBottomCover;
    @BindView(R.id.tv_gallery_title)
    TextView tvTitle;
    @BindView(R.id.pb_gallery_image_progress)
    ProgressBar pbImageProgress;

    private ImageGalleryPagerAdapter mCoverPagerAdapter;
    private int mIndex;

    public static final String REVIEW_ID = "com.checkin.app.checkin.RestaurantImage.reviewid";
    public static final String INDEX = "com.checkin.app.checkin.RestaurantImage.index";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        ButterKnife.bind(this);

        Intent imageGalleryIntent = getIntent();
        long reviewId = imageGalleryIntent.getLongExtra(REVIEW_ID, 0L);
        mIndex = imageGalleryIntent.getIntExtra(INDEX, 0);

        ImageGalleryViewModel mViewModel = ViewModelProviders.of(this).get(ImageGalleryViewModel.class);

        mCoverPagerAdapter = new ImageGalleryPagerAdapter(getApplicationContext(), pbImageProgress);
        vpImagePagerCover.setAdapter(mCoverPagerAdapter);

        indicatorBottomCover.setViewPager(vpImagePagerCover);
        indicatorBottomCover.setAnimationType(AnimationType.FILL);
        indicatorBottomCover.setClickListener(position -> vpImagePagerCover.setCurrentItem(position));

        mViewModel.getReviewImages(reviewId);
        mViewModel.getImageGalleryModel().observe(this, input -> {
            if (input == null) return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                this.setupData(input.data);
            }
        });

        ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void setupData(@NonNull ImageGalleryModel data) {
        tvUploadUserName.setText(data.getUploader().getDisplayName());
        tvTitle.setText(data.getTitle());
        if (data.getImages() != null) {
            mCoverPagerAdapter.setData(data.getImages());
            if (mIndex < data.getImages().size())
                vpImagePagerCover.setCurrentItem(mIndex, false);
        }
    }
}

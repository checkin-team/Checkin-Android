package com.checkin.app.checkin.RestaurantImage;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantImageActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_upload_user_name)
    TextView tvUploadUserName;
    @BindView(R.id.ll_toolbar)
    LinearLayout llToolbar;
    @BindView(R.id.v_top_line)
    View vTopLine;
    @BindView(R.id.vp_image_pager_cover)
    ViewPager vpImagePagerCover;
    @BindView(R.id.indicator_bottom_cover)
    PageIndicatorView indicatorBottomCover;
    @BindView(R.id.v_bottom_line)
    View vBottomLine;
    @BindView(R.id.tv_item_name)
    TextView tvItemName;
    @BindView(R.id.pb_image_progress)
    ProgressBar pbImageProgress;

    private ImageGalleryPagerAdapter mCoverPagerAdapter;

    public static final String REVIEW_ID = "com.checkin.app.checkin.RestaurantImage.reviewid";
    public static final String INDEX = "com.checkin.app.checkin.RestaurantImage.index";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image);
        ButterKnife.bind(this);

        Intent imageGalleryIntent = getIntent();
        Long reviewId = imageGalleryIntent.getLongExtra(REVIEW_ID,0L);
        int index = imageGalleryIntent.getIntExtra(INDEX,0);

        ImageGalleryViewModel mViewModel = ViewModelProviders.of(this).get(ImageGalleryViewModel.class);

        mCoverPagerAdapter = new ImageGalleryPagerAdapter(getApplicationContext(),pbImageProgress);
        vpImagePagerCover.setAdapter(mCoverPagerAdapter);

        indicatorBottomCover.setViewPager(vpImagePagerCover);
        indicatorBottomCover.setAnimationType(AnimationType.FILL);
        indicatorBottomCover.setClickListener(position -> vpImagePagerCover.setCurrentItem(position));

        mViewModel.getImageGalleryById(String.valueOf(reviewId));
        mViewModel.getImageGalleryModel().observe(this,input -> {
            if (input == null) return;
            if (input.status == Resource.Status.SUCCESS && input.data != null){
                String itemName = input.data.getTitle();
                String userName = input.data.getUploader().getDisplayName();
                tvUploadUserName.setText(userName);
                tvItemName.setText(itemName);
                List<String> imageList = input.data.getImages();
                if (imageList != null && imageList.size() > 0)
                    this.setupData(imageList);
            }
        });

        ivBack.setOnClickListener( view -> onBackPressed());
    }

    private void setupData(List<String> list) {
        String[] imageArray = new String[list.size()];
        imageArray = list.toArray(imageArray);
        mCoverPagerAdapter.setData(imageArray);
    }
}

package com.checkin.app.checkin.User.PersonalProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.PrivateProfile.FollowersActivity;
import com.checkin.app.checkin.Utility.SelectCropImageActivity;
import com.checkin.app.checkin.Utility.Util;
import com.transitionseverywhere.TransitionManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfilePersonalActivity extends AppCompatActivity {
    public static final String KEY_PROFILE_USER_ID = "profile_user_id";
    private static final String TAG = "UserProfileActivity";

    @BindView(R.id.root_view)
    ViewGroup vRoot;
    @BindView(R.id.container_description) ViewGroup vDescription;
    @BindView(R.id.im_profile)ImageView imProfile;
    @BindView(R.id.tv_checkins)TextView tvCheckins;
    @BindView(R.id.tv_reviews) TextView tvReviews;
    @BindView(R.id.tv_followers)TextView tvFollowers;
    @BindView(R.id.tv_bio)TextView tvBio;
    @BindView(R.id.tv_display_name)TextView tvDisplayName;
    @BindView(R.id.tv_city)TextView tvCity;
    @BindView(R.id.tv_hobby)TextView tvProfession;
//    @BindView(R.id.btn_edit_profile)TextView btnEditProfile;
    @BindView(R.id.guideline_above)
    Guideline vGuidelineAbove;
    @BindView(R.id.guideline_below) Guideline vGuidelineBelow;
    private UserViewModel mViewModel;
    private UserModel person;
    float currY,  maxPercentDiff = 0.1f;
    float origAbovePercent = .67f, currAbovePercent = .67f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_personal);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        if (getIntent().getExtras() == null)
            return;

        long userId = getIntent().getExtras().getLong(KEY_PROFILE_USER_ID);

//        mViewModel.getUser(userId).observe(this, userModel -> {
//            if (userModel == null) return;
//            if (userModel.status == Resource.Status.SUCCESS) {
//                if (userModel.data != null) {
//                    person = userModel.data;
//                    UserProfilePersonalActivity.this.setUI(person);
//                }
//            } else if (userModel.status == Resource.Status.LOADING) {
//                //rv.setAdapter(new ReviewsAdapter(reviewsModel.data));
//                // LOADING
//            } else {
//            }
//
//        });
    }

//    @OnClick(R.id.btn_edit_profile)
//    public void editProfile()
//    {
//        startActivity(new Intent(getApplicationContext(),EditPersonalProfile.class));
//    }

    @OnClick(R.id.im_edit_profile_pic)
    public void editProfilePic()
    {
        Intent intent;
        intent = new Intent(this , SelectCropImageActivity.class);
        startActivityForResult(intent,SelectCropImageActivity.KEY_CROP_IMAGE_REQUEST_CODE);
    }
    @OnClick({R.id.tv_followers, R.id.tv_label_followers})
    public void followers(){
        startActivity(new Intent(getApplicationContext(), FollowersActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== SelectCropImageActivity.KEY_CROP_IMAGE_REQUEST_CODE&&resultCode==RESULT_OK)
        {
            File rectangleFile = data.getParcelableExtra(SelectCropImageActivity.KEY_RECTANGLE_IMAGE);
            mViewModel.postImages(rectangleFile);
        }
    }


    public  void setUI(UserModel person){
        Glide.with(UserProfilePersonalActivity.this).load(person.getProfilePic()).into(imProfile);
        tvFollowers.setText(person.formatFollowers());
        tvCheckins.setText(person.formatCheckins());
        tvReviews.setText(person.formatReviews());
        tvBio.setText(person.getBio());
        tvDisplayName.setText(person.getUsername());
        tvCity.setText(person.getAddress());

    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        int screenHeight = vRoot.getHeight();
        float diffPercentY;
        Log.e(TAG, "currPercentAbove: " + currAbovePercent);
        ConstraintLayout.LayoutParams paramsAbove, paramsBelow;
        switch(event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                currY = event.getY();
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "Action was MOVE");

                diffPercentY = (event.getY() - currY) / screenHeight;
                Log.e(TAG, "diffY: " + diffPercentY + " screenHeight: " + screenHeight);

                if (currAbovePercent > origAbovePercent || currAbovePercent < origAbovePercent - maxPercentDiff || Math.abs(diffPercentY) >= maxPercentDiff)
                    return true;

                paramsAbove = (ConstraintLayout.LayoutParams) vGuidelineAbove.getLayoutParams();
                paramsBelow = (ConstraintLayout.LayoutParams) vGuidelineBelow.getLayoutParams();
                currAbovePercent += diffPercentY;
                if (currAbovePercent < origAbovePercent - maxPercentDiff)
                    currAbovePercent = origAbovePercent - maxPercentDiff;
                else if (currAbovePercent > origAbovePercent)
                    currAbovePercent = origAbovePercent;
                paramsAbove.guidePercent = currAbovePercent;
                paramsBelow.guidePercent = currAbovePercent + .03f;

                vGuidelineBelow.setLayoutParams(paramsBelow);
                vGuidelineAbove.setLayoutParams(paramsAbove);

                handleUserInfoTransition((origAbovePercent - currAbovePercent) / maxPercentDiff);

                return true;
            case (MotionEvent.ACTION_UP):
                float diff = event.getY() - currY;
                if (diff < maxPercentDiff) {
                    boolean isDown = diff > 0;
                    Log.e(TAG, "isDown: " + isDown);
                    paramsAbove = (ConstraintLayout.LayoutParams) vGuidelineAbove.getLayoutParams();
                    paramsBelow = (ConstraintLayout.LayoutParams) vGuidelineBelow.getLayoutParams();

                    TransitionManager.beginDelayedTransition(vRoot);

                    paramsAbove.guidePercent = (isDown) ? origAbovePercent : origAbovePercent - maxPercentDiff;
                    paramsBelow.guidePercent = (isDown) ? origAbovePercent + .03f : origAbovePercent + .03f - maxPercentDiff;

                    Log.e(TAG, "above: " + paramsAbove.guidePercent + ", below: " + paramsBelow.guidePercent);

                    vGuidelineBelow.setLayoutParams(paramsBelow);
                    vGuidelineAbove.setLayoutParams(paramsAbove);
                }
        }
        return super.onTouchEvent(event);
    }

    private void handleUserInfoTransition(float alpha) {
        vDescription.setAlpha(1 - alpha);
    }

    @OnClick(R.id.btn_back)
    public void goBack(View v){
        onBackPressed();
    }
}

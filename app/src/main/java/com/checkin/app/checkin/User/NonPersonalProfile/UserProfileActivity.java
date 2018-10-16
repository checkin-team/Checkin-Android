package com.checkin.app.checkin.User.NonPersonalProfile;

/**
 * Created by TAIYAB on 10-04-2018.
 */


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.PrivateProfile.FollowersActivity;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.Util;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

    public static final String KEY_PROFILE_USER_ID = "profile_user_id";

    @BindView(R.id.root_view) ViewGroup vRoot;
    @BindView(R.id.container_description) ViewGroup vDescription;
    @BindView(R.id.im_profile)ImageView imProfile;
    @BindView(R.id.tv_checkins)TextView tvCheckins;
    @BindView(R.id.tv_reviews) TextView tvReviews;
    @BindView(R.id.tv_followers)TextView tvFollowers;
    @BindView(R.id.tv_bio)TextView tvBio;
    @BindView(R.id.tv_display_name)TextView tvDisplayName;
    @BindView(R.id.tv_city)TextView tvCity;
//    @BindView(R.id.btn_follow)TextView btnFollow;
    @BindView(R.id.guideline_above) Guideline vGuidelineAbove;
    @BindView(R.id.guideline_below) Guideline vGuidelineBelow;
//    @BindView(R.id.btn_message) TextView btnMessage;
//    @BindView(R.id.btn_request_sent) TextView btnRequestSent;
//    @BindView(R.id.im_btn_following) ImageView btnUnfollow;

    private UserViewModel mViewModel;
    float currY,  maxPercentDiff = 0.1f;
    float origAbovePercent = .67f, currAbovePercent = .67f;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_non_personal);
        ButterKnife.bind(this);

        mViewModel= ViewModelProviders.of(this).get(UserViewModel.class);

        if (getIntent().getExtras() == null)
            return;

        long userId = getIntent().getExtras().getLong(KEY_PROFILE_USER_ID);

        mViewModel.getUser(userId).observe(this, userModel -> {
            if (userModel == null) return;
            if (userModel.status == Resource.Status.SUCCESS && userModel.data != null) {
                UserProfileActivity.this.setUI(userModel.data);
            } else if (userModel.status == Resource.Status.LOADING) {
                // LOADING
            }
        });
    }

    public  void setUI(UserModel person){
        if (person.getProfilePic() != null)
            GlideApp.with(this).load(person.getProfilePic()).into(imProfile);
        else if (person.getGender() == UserModel.GENDER.MALE)
            GlideApp.with(this).load(R.drawable.cover_unknown_male).into(imProfile);
        else
            GlideApp.with(this).load(R.drawable.cover_unknown_female).into(imProfile);
        tvFollowers.setText(person.formatFollowers());
        tvCheckins.setText(person.formatCheckins());
        tvReviews.setText(person.formatReviews());
        tvBio.setText(person.getBio());
        tvDisplayName.setText(person.getFullName());
        tvCity.setText(person.getAddress());
//        if (person.isConnected()) {
//            setMessageButton();
//        } else if (person.isPublic()) {
//            setFollowButton();
//        } else {
//            setRequestConnectionButton();
//        }
    }

//    private void setFollowButton() {
//        btnFollow.setVisibility(View.VISIBLE);
//        btnFollow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnFollow.setVisibility(View.INVISIBLE);
//                setMessageButton();
//                person.setConnected(true);
//            }
//        });
//    }
//
//
//    private void setRequestConnectionButton() {
//        btnRequestSent.setVisibility(View.VISIBLE);
//    }
//
//    private void setMessageButton() {
//        btnMessage.setVisibility(View.VISIBLE);
//
//        btnMessage.setTranslationX(-Util.dpToPx(40)*1f);
//
//        TransitionManager.beginDelayedTransition(findViewById(R.id.container_header));
//        btnUnfollow.setVisibility(View.VISIBLE);
//    }
//
//    @OnClick(R.id.im_btn_following)
//    public void onUnfollow() {
//        btnUnfollow.setVisibility(View.INVISIBLE);
//        setFollowButton();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        int screenHeight = vRoot.getHeight();
//        float diffPercentY;
//        Log.e(TAG, "currPercentAbove: " + currAbovePercent);
//        ConstraintLayout.LayoutParams paramsAbove, paramsBelow;
//        switch(event.getAction()) {
//            case (MotionEvent.ACTION_DOWN):
//                currY = event.getY();
//                return true;
//            case (MotionEvent.ACTION_MOVE):
//                Log.d(TAG, "Action was MOVE");
//
//                diffPercentY = (event.getY() - currY) / screenHeight;
//                Log.e(TAG, "diffY: " + diffPercentY + " screenHeight: " + screenHeight);
//
//                if (currAbovePercent > origAbovePercent || currAbovePercent < origAbovePercent - maxPercentDiff || Math.abs(diffPercentY) >= maxPercentDiff)
//                    return true;
//
//                paramsAbove = (ConstraintLayout.LayoutParams) vGuidelineAbove.getLayoutParams();
//                paramsBelow = (ConstraintLayout.LayoutParams) vGuidelineBelow.getLayoutParams();
//                currAbovePercent += diffPercentY;
//                if (currAbovePercent < origAbovePercent - maxPercentDiff)
//                    currAbovePercent = origAbovePercent - maxPercentDiff;
//                else if (currAbovePercent > origAbovePercent)
//                    currAbovePercent = origAbovePercent;
//                paramsAbove.guidePercent = currAbovePercent;
//                paramsBelow.guidePercent = currAbovePercent + .03f;
//
//                vGuidelineBelow.setLayoutParams(paramsBelow);
//                vGuidelineAbove.setLayoutParams(paramsAbove);
//
//                handleUserInfoTransition((origAbovePercent - currAbovePercent) / maxPercentDiff);
//
//                return true;
//            case (MotionEvent.ACTION_UP):
//                float diff = event.getY() - currY;
//                if (diff < maxPercentDiff) {
//                    boolean isDown = diff > 0;
//                    Log.e(TAG, "isDown: " + isDown);
//                    paramsAbove = (ConstraintLayout.LayoutParams) vGuidelineAbove.getLayoutParams();
//                    paramsBelow = (ConstraintLayout.LayoutParams) vGuidelineBelow.getLayoutParams();
//
//                    TransitionManager.beginDelayedTransition(vRoot);
//
//                    paramsAbove.guidePercent = (isDown) ? origAbovePercent : origAbovePercent - maxPercentDiff;
//                    paramsBelow.guidePercent = (isDown) ? origAbovePercent + .03f : origAbovePercent + .03f - maxPercentDiff;
//
//                    Log.e(TAG, "above: " + paramsAbove.guidePercent + ", below: " + paramsBelow.guidePercent);
//
//                    vGuidelineBelow.setLayoutParams(paramsBelow);
//                    vGuidelineAbove.setLayoutParams(paramsAbove);
//                }
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private void handleUserInfoTransition(float alpha) {
//        vDescription.setAlpha(1 - alpha);
//    }

    @OnClick(R.id.btn_back)
    public void goBack(View v){
        onBackPressed();
    }
}

package com.checkin.app.checkin.User.NonPersonalProfile;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.PrivateProfile.FollowersActivity;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserModel.FRIEND_STATUS;
import com.checkin.app.checkin.Utility.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

    public static final String KEY_PROFILE_USER_ID = "profile_user_id";

    @BindView(R.id.im_profile) ImageView imProfile;
    @BindView(R.id.tv_checkins)TextView tvCheckins;
    @BindView(R.id.tv_reviews) TextView tvReviews;
    @BindView(R.id.tv_followers)TextView tvFollowers;
    @BindView(R.id.tv_bio) TextView tvBio;
    @BindView(R.id.tv_display_name) TextView tvDisplayName;
    @BindView(R.id.tv_city) TextView tvCity;
//    @BindView(R.id.guideline_above) Guideline vGuidelineAbove;
//    @BindView(R.id.guideline_below) Guideline vGuidelineBelow;

    private UserViewModel mViewModel;
//    float currY,  maxPercentDiff = 0.1f;
//    float origAbovePercent = .67f, currAbovePercent = .67f;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_non_personal);
        ButterKnife.bind(this);

        if (getIntent().getExtras() == null)
            return;

        init(R.id.root_view, true);

        mViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mViewModel.setUserPk(getIntent().getExtras().getLong(KEY_PROFILE_USER_ID));
        mViewModel.getUser().observe(this, userModelResource -> {
            if (userModelResource == null) return;
            if (userModelResource.status == Resource.Status.SUCCESS && userModelResource.data != null) {
                setUI(userModelResource.data);
                doneLoad();
            } else if (userModelResource.status == Resource.Status.LOADING) {
                initLoad();
            } else {
                alertStatus(userModelResource.status, userModelResource.message);
            }
        });
        mViewModel.getObservableData().observe(this, resource -> {
            assert resource != null;
            if (resource.status == Resource.Status.SUCCESS)
                mViewModel.updateResults();
        });
    }

    private void setUI(UserModel person) {
        if (person.getGender() == UserModel.GENDER.MALE)
            imProfile.setImageResource(R.drawable.cover_unknown_male);
        else
            imProfile.setImageResource(R.drawable.cover_unknown_female);
        if (person.getProfilePic() != null)
            GlideApp.with(this).load(person.getProfilePic()).into(imProfile);
        tvFollowers.setText(person.formatFollowers());
        tvCheckins.setText(person.formatCheckins());
        tvReviews.setText(person.formatReviews());
        tvBio.setText(person.getBio());
        tvDisplayName.setText(person.getFullName());
        tvCity.setText(person.getAddress());
        setFriendshipAction(person.getFriendStatus());
    }

    private void setFriendshipAction(FRIEND_STATUS friendStatus) {
        if (friendStatus == FRIEND_STATUS.NONE) {
            findViewById(R.id.container_status_none).setVisibility(View.VISIBLE);
        } else if (friendStatus == FRIEND_STATUS.PENDING_REQUEST) {
            findViewById(R.id.container_status_request).setVisibility(View.VISIBLE);
        } else if (friendStatus == FRIEND_STATUS.FRIENDS) {
            findViewById(R.id.container_status_friend).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_follow)
    public void onFollow() {
        Toast.makeText(getApplicationContext(), "Currently unsupported action!", Toast.LENGTH_SHORT).show();
        // TODO: With friendship model.
    }

    @OnClick(R.id.btn_message)
    public void onMessage() {
        Toast.makeText(getApplicationContext(), "Currently unsupported action!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.im_btn_following)
    public void onUnfollow() {
        Toast.makeText(getApplicationContext(), "Currently unsupported action!", Toast.LENGTH_SHORT).show();
        // TODO: With friendship model.
    }

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

package com.checkin.app.checkin.User.NonPersonalProfile;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.Friendship.FriendsListActivity;
import com.checkin.app.checkin.User.Friendship.FriendshipModel.FRIEND_STATUS;
import com.checkin.app.checkin.User.Friendship.FriendshipRequestModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.GlideApp;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

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
    private MaterialStyledDialog mMessageDialog;
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
            else if (resource.status != Resource.Status.LOADING) {
                Toast.makeText(getApplicationContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUI(UserModel person) {
        setupMessageDialog();
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
        findViewById(R.id.container_status_none).setVisibility(View.GONE);
        findViewById(R.id.container_status_request).setVisibility(View.GONE);
        findViewById(R.id.container_status_friend).setVisibility(View.GONE);
        setFriendshipAction(person);
    }

    private void setFriendshipAction(UserModel person) {
        FRIEND_STATUS friendStatus = person.getFriendStatus();
        FriendshipRequestModel requestModel = person.getFriendshipRequest();
        if (friendStatus == FRIEND_STATUS.NONE) {
            findViewById(R.id.container_status_none).setVisibility(View.VISIBLE);
        } else if (friendStatus == FRIEND_STATUS.PENDING_REQUEST) {
            findViewById(R.id.container_status_request).setVisibility(View.VISIBLE);
            if (requestModel == null) {
                Log.e(TAG, "Request Model is null!!!");
                return;
            }
            updateRequestActions(requestModel.getToUser() == person.getId());
        } else if (friendStatus == FRIEND_STATUS.FRIENDS) {
            findViewById(R.id.container_status_friend).setVisibility(View.VISIBLE);
        }
    }

    private void updateRequestActions(boolean isCurrentUserTheSenderOfRequest) {
        findViewById(R.id.container_request_sender).setVisibility(isCurrentUserTheSenderOfRequest ? View.VISIBLE : View.GONE);
        findViewById(R.id.container_request_recepient).setVisibility(isCurrentUserTheSenderOfRequest ? View.GONE : View.VISIBLE);
    }

    private void setupMessageDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.view_input_text, null);
        EditText edMessage = view.findViewById(R.id.ed_input);
        mMessageDialog = new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Enter message")
                .setPositiveText("Request")
                .setNegativeText("Cancel")
                .onPositive((dialog, which) -> {
                    String message = edMessage.getText().toString();
                    mViewModel.addFriend(message);
                })
                .setCustomView(view)
                .build();
    }

    @OnClick(R.id.btn_follow)
    public void onFollow() {
        mMessageDialog.show();
    }

    @OnClick(R.id.btn_message)
    public void onMessage() {
        Toast.makeText(getApplicationContext(), "Currently unsupported action!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.im_btn_following)
    public void onUnfollow() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to remove connection?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.removeFriend();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @OnClick(R.id.btn_accept)
    public void onAcceptRequest() {
        mViewModel.acceptFriendRequest();
    }

    @OnClick(R.id.btn_reject)
    public void onRejectRequest() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to reject pending request?")
                .setPositiveButton("Reject", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.rejectFriendRequest();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelRequest() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setMessage("Do you want to cancel pending request?")
                .setPositiveButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    mViewModel.cancelFriendRequest();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @OnClick(R.id.container_followers)
    public void onShowFriends() {
        Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
        intent.putExtra(FriendsListActivity.KEY_USER_PK, mViewModel.getPk());
        startActivity(intent);
    }

    @OnClick(R.id.container_checkins)
    public void onShowCheckins() {
        startActivity(new Intent(getApplicationContext(), UserCheckinsActivity.class));
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

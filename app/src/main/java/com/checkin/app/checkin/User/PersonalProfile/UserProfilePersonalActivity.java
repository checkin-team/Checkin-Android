package com.checkin.app.checkin.User.PersonalProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.PrivateProfile.FollowersActivity;
import com.checkin.app.checkin.Utility.SelectCropImageActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfilePersonalActivity extends AppCompatActivity {
    UserViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_personal);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
    }

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
}

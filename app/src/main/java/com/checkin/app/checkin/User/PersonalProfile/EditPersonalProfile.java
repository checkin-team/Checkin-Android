package com.checkin.app.checkin.User.PersonalProfile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPersonalProfile extends AppCompatActivity {

    @BindView(R.id.btn_back)Button btnBack;
    @BindView(R.id.btn_done)Button btnDone;
    @BindView(R.id.et_name)EditText etName;
    @BindView(R.id.et_location)EditText etLocation;
    @BindView(R.id.et_bio)EditText etBio;
    @BindView(R.id.et_phoneNumber)EditText etPhone;
    @BindView(R.id.et_email)EditText etEmail;
    UserViewModel mUserViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_profile);
        ButterKnife.bind(this);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mUserViewModel.getCurrentUser().observe(this, userModelResource -> {
            if (userModelResource != null && userModelResource.status == Resource.Status.SUCCESS) {
                setUi(userModelResource.data);
            }
        });
    }

    @OnClick(R.id.btn_done)
    public void updatingInfo()
    {
        //take respective fields and send to server
    }

    private void setUi(UserModel mUserModel) {
        etName.setText(mUserModel.getUsername());
        etLocation.setText(mUserModel.getLocation());
        etBio.setText(mUserModel.getBio());
        //etPhone.setText(mUserModel.getPhoneNumber);
        //etEmail.setText(mUserModel.getEmail);

    }
    @OnClick(R.id.btn_back)
    public void goBack()
    {
        finish();
    }
}

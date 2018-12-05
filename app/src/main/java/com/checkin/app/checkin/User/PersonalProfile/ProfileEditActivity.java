package com.checkin.app.checkin.User.PersonalProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.OtpVerificationDialog;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileEditActivity extends AppCompatActivity implements OtpVerificationDialog.AuthCallback {
    private static final String TAG = ProfileEditActivity.class.getSimpleName();

    @BindView(R.id.et_first_name) EditText etFirstName;
    @BindView(R.id.et_last_name) EditText etLastName;
    @BindView(R.id.et_location) EditText etLocation;
    @BindView(R.id.et_bio) EditText etBio;
    @BindView(R.id.et_phone) EditText etPhone;
    @BindView(R.id.tv_username) TextView tvUsername;

    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_appbar_back);
        actionBar.setElevation(10);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);

        mUserViewModel.getUser().observe(this, userModelResource -> {
            if (userModelResource.status == Resource.Status.SUCCESS && userModelResource.data != null) {
                setUi(userModelResource.data);
            }
        });
        mUserViewModel.getObservableData().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }


    private void setUi(UserModel user) {
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        etLocation.setText(user.getAddress());
        etBio.setText(user.getBio());
        etPhone.setText(user.getPhoneNumber());
        tvUsername.setText("Username: " + user.getUsername());
        //etEmail.setText(mUserModel.getEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done: {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String location = etLocation.getText().toString();
                String bio = etBio.getText().toString();
                mUserViewModel.postUserData(firstName, lastName, location, bio);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onSuccessVerification(DialogInterface dialog, PhoneAuthCredential credential) {

    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {

    }

    @Override
    public void onFailedVerification(DialogInterface dialog, FirebaseException exception) {

    }
}

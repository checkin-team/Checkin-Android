package com.checkin.app.checkin.User.Private;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileEditActivity extends AppCompatActivity implements OtpVerificationDialog.AuthCallback {

    public static final String FIRST_NAME = "com.checkin.app.checkin.User.Private.first_name";
    public static final String LAST_NAME = "com.checkin.app.checkin.User.Private.last_name";
    public static final String USERNAME = "com.checkin.app.checkin.User.Private.username";
    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    @BindView(R.id.et_first_name)
    EditText etFirstName;
    @BindView(R.id.et_last_name)
    EditText etLastName;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
            actionBar.setElevation(10);
        }

        Intent userIntent = getIntent();

        String firstName = userIntent.getStringExtra(FIRST_NAME);
        String lastName = userIntent.getStringExtra(LAST_NAME);
        String userName = userIntent.getStringExtra(USERNAME);

        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        tvUser.setText(userName);

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mUserViewModel.getUserData().observe(this, userModelResource -> {
            if (userModelResource == null)
                return;
            if (userModelResource.status == Resource.Status.SUCCESS && userModelResource.data != null) {
                setUi(userModelResource.data);
            }
        });
        mUserViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS) {
                finish();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }


    private void setUi(UserModel user) {
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        tvUsername.setText("Username: " + user.getUsername());
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
                mUserViewModel.postUserData(firstName, lastName);
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

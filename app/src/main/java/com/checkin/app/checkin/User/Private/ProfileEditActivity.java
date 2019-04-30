package com.checkin.app.checkin.User.Private;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.OtpVerificationDialog;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileEditActivity extends AppCompatActivity implements OtpVerificationDialog.AuthCallback {

    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    @BindView(R.id.et_edit_profile_name)
    EditText etName;
    @BindView(R.id.et_edit_profile_city)
    EditText etCity;
    @BindView(R.id.et_edit_profile_country)
    EditText etCountry;
    @BindView(R.id.et_edit_profile_bio)
    EditText etBio;
    @BindView(R.id.et_edit_profile_phone)
    EditText etPhone;
    @BindView(R.id.et_edit_profile_email)
    EditText etEmail;
    @BindView(R.id.tv_edit_save_phone)
    TextView tvEditPhone;

    private UserViewModel mUserViewModel;
    private FirebaseAuth mAuth;
    private String phone_token = "";

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

        getData();

        mAuth = FirebaseAuth.getInstance();
    }

    private void getData(){
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.fetchUserData();
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
            } else {
                Utils.toast(this, resource.message);
            }
        });

    }


    private void setUi(UserModel user) {
        etName.setText(user.getFullName());
        etCity.setText(user.getAddress());
        etCountry.setText("India");
        etBio.setText(user.getBio());
        etPhone.setText(user.getPhoneNumber());
    }

    @OnClick(R.id.tv_edit_save_phone)
    public void onEditPhone() {

        if (tvEditPhone.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_save)) &&
                etPhone.getText().toString().length() == 13) {
            OtpVerificationDialog dialog = OtpVerificationDialog.Builder.with(this)
                    .setAuthCallback(this)
                    .build();
            dialog.verifyPhoneNumber(etPhone.getText().toString());
            dialog.show();
        } else {
            etPhone.setEnabled(true);
            etPhone.requestFocus();
            setPhoneCode();
            disablePhoneSaveButton();
        }

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //beforeTextChanged code
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //onTextChanged code
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91"))
                    setPhoneCode();

                if (s.length() >= 13) {
                    tvEditPhone.setEnabled(true);
                    tvEditPhone.setTextColor(getResources().getColor(R.color.primary_red));
                } else {
                    disablePhoneSaveButton();
                }

            }
        });
    }

    public void disablePhoneSaveButton() {
        tvEditPhone.setText(getResources().getString(R.string.btn_save));
        tvEditPhone.setEnabled(false);
        tvEditPhone.setTextColor(getResources().getColor(R.color.pinkish_grey));
    }

    public void setPhoneCode() {
        etPhone.setText("+91");
        Selection.setSelection(etPhone.getText(), etPhone.getText().length());
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
                String name = etName.getText().toString();
                String firstName="";
                String lastName="";
                if(name.split("\\w+").length>1){

                    lastName = name.substring(name.lastIndexOf(" ")+1);
                    firstName = name.substring(0, name.lastIndexOf(' '));
                }
                else{
                    firstName = name;
                }
                mUserViewModel.postUserData(firstName, lastName, phone_token, etBio.getText().toString());
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

        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(result -> {
                    phone_token = result.getToken();
                });
            } else {
                Log.e(TAG, "Authentication failed", task.getException());
                Toast.makeText(getApplicationContext(), R.string.error_authentication, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.dismiss();
    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {
    }

    @Override
    public void onFailedVerification(DialogInterface dialog, FirebaseException exception) {
        Utils.toast(getApplicationContext(), exception.getMessage());
        dialog.dismiss();
    }
}

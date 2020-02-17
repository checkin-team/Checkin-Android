package com.checkin.app.checkin.user.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException;
import com.checkin.app.checkin.auth.fragments.OtpVerificationDialog;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.user.models.UserModel;
import com.checkin.app.checkin.user.viewmodels.UserViewModel;
import com.checkin.app.checkin.utility.Utils;
import com.google.firebase.auth.PhoneAuthCredential;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileEditActivity extends AppCompatActivity implements OtpVerificationDialog.AuthCallback {

    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    public static final String KEY_USER_DATA = "profileEdit.user_data";
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
    @BindView(R.id.tv_error_phone_number)
    TextView tvErrorPhoneNumber;

    private UserViewModel mUserViewModel;
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
    }

    private void getData() {
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        setUi((UserModel) getIntent().getSerializableExtra(KEY_USER_DATA));

        mUserViewModel.getUserData().observe(this, userModelResource -> {
            if (userModelResource == null)
                return;
            if (userModelResource.getStatus() == Resource.Status.SUCCESS && userModelResource.getData() != null) {
                setUi(userModelResource.getData());
                finish();
            } else if (userModelResource.getStatus() == Resource.Status.ERROR_INVALID_REQUEST) {
                tvErrorPhoneNumber.setText(userModelResource.getMessage());
                tvErrorPhoneNumber.setVisibility(View.VISIBLE);
                Utils.setKeyboardVisibility(etPhone, false);
                enablePhoneSaveButton(getResources().getString(R.string.btn_edit));
            }
            if (userModelResource.getStatus() != Resource.Status.LOADING) {
                Utils.toast(this, userModelResource.getMessage());
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
        tvErrorPhoneNumber.setVisibility(View.GONE);
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
            disablePhoneSaveButton(getResources().getString(R.string.btn_save));
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
                    enablePhoneSaveButton(getResources().getString(R.string.btn_save));
                } else {
                    disablePhoneSaveButton(getResources().getString(R.string.btn_save));
                }
            }
        });
    }

    public void disablePhoneSaveButton(String text) {
        tvEditPhone.setText(text);
        tvEditPhone.setEnabled(false);
        tvEditPhone.setTextColor(getResources().getColor(R.color.pinkish_grey));
    }

    public void enablePhoneSaveButton(String text) {
        tvEditPhone.setText(text);
        tvEditPhone.setEnabled(true);
        tvEditPhone.setTextColor(getResources().getColor(R.color.primary_red));
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
                hitApiSaveProfile();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void hitApiSaveProfile() {
        String name = etName.getText().toString().trim();
        String firstName = "";
        String lastName = "";
        if (name.split("\\w+").length > 1) {

            lastName = name.substring(name.lastIndexOf(" ") + 1);
            firstName = name.substring(0, name.lastIndexOf(' '));
        } else {
            firstName = name;
        }
        mUserViewModel.postUserData(firstName, lastName, phone_token, etBio.getText().toString().trim());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onSuccessVerification(DialogInterface dialog, @NotNull PhoneAuthCredential credential, @NotNull String idToken) {
        phone_token = idToken;
        hitApiSaveProfile();
        disablePhoneSaveButton("Saved");
    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {
    }

    @Override
    public void onFailedVerification(DialogInterface dialog, Exception exception) {
        Utils.toast(this, exception.getLocalizedMessage());
        if (!(exception instanceof InvalidOTPException)) dialog.dismiss();
    }
}

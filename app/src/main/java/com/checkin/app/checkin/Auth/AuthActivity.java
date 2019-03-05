package com.checkin.app.checkin.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.Misc.EulaDialog;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends AppCompatActivity implements AuthFragmentInteraction, OtpVerificationDialog.AuthCallback {
    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int RC_AUTH_GOOGLE = 1000;

    @BindView(R.id.circle_progress)
    ProgressBar vCircleProgress;
    @BindView(R.id.dark_back)
    View mDarkBack;
    @BindView(R.id.tv_read_eula)
    CheckedTextView ctvReadEula;

    private CallbackManager mFacebookCallbackManager;
    private FirebaseAuth mAuth;
    private AuthViewModel mAuthViewModel;

    private boolean goBack = true;
    private EulaDialog eulaDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mFacebookCallbackManager = CallbackManager.Factory.create();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, AuthOptionsFragment.newInstance(this, mFacebookCallbackManager));
            transaction.commit();
        }

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            Log.v(TAG, "User already exists.");
        }

        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);

        mAuthViewModel.getAuthResult().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS:
                    assert resource.data != null;
                    hideProgress();
                    Utils.toast(this, "Welcome!");
                    successAuth(resource.data);
                    break;
                case LOADING:
                    showProgress();
                    break;
                case ERROR_INVALID_REQUEST:
                    if (mAuthViewModel.isLoginAttempt()) {
                        askUserDetails();
                    } else {
                        JsonNode error = resource.getErrorBody();
                        if (error == null)
                            return;
                        if (error.has("errors")) {
                            String msg = error.get("errors").get(0).asText();
                            Utils.toast(this, String.format(Locale.ENGLISH, "%s\nTry again.", msg));
                        } else if (error.has("username")) {
                            mAuthViewModel.showError(error);
                        } else Utils.toast(this, resource.message);
                    }
                    break;
                default:
                    hideProgress();
                    Utils.toast(this, resource.message);
            }
        });

        mDarkBack.setOnTouchListener((v, event) -> true);
        eulaDialog = new EulaDialog(this, ctvReadEula::setChecked);
    }

    private void showProgress() {
        vCircleProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        vCircleProgress.setVisibility(View.GONE);
    }

    private void authenticateWithCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        assert mAuth.getCurrentUser() != null;

                        mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(tokenResult -> {
                            mAuthViewModel.setFireBaseIdToken(tokenResult.getToken());
                            mAuthViewModel.login();
                        });
                    } else {
                        Log.e(TAG, "Authentication failed", task.getException());
                        Toast.makeText(getApplicationContext(), R.string.error_authentication, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void askUserDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        Fragment fragment = SignupUserInfoFragment.newInstance(AuthActivity.this);
        if (user == null) {
            Log.e(TAG, "Logged-in user is NULL");
            return;
        }
        String name = user.getDisplayName();
        if (name != null) {
            Bundle bundle = new Bundle();
            bundle.putString(SignupUserInfoFragment.KEY_NAME, name);
            fragment.setArguments(bundle);
        }
        goBack = false;
        showDarkBack();
        hideProgress();
        replaceFragmentContainer(fragment);
    }

    private void replaceFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showDarkBack() {
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void hideDarkBack() {
        mDarkBack.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.parent_layout)
    public void outClick(){
        Utils.hideSoftKeyboard(this);
    }

    @OnClick(R.id.tv_read_eula)
    public void readEula() {
        eulaDialog.show();
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String userName, GENDER gender) {
        mAuthViewModel.register(firstName, lastName, gender, userName);
    }

    private boolean canLogin() {
        if (!ctvReadEula.isChecked()) {
            Utils.toast(this, "Need to accept EULA.");
            return false;
        }
        return true;
    }

    @Override
    public void onGoogleAuth() {
        if (!canLogin())
            return;
        showProgress();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_AUTH_GOOGLE);
    }

    @Override
    public void onFacebookAuth(LoginResult loginResult) {
        if (!canLogin())
            return;
        showProgress();

        AccessToken accessToken = loginResult.getAccessToken();

        mAuthViewModel.setProviderIdToken(accessToken.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        authenticateWithCredential(credential);
    }

    @Override
    public void onPhoneAuth(String phoneNo) {
        if (!canLogin())
            return;
        OtpVerificationDialog dialog = OtpVerificationDialog.Builder.with(this)
                .setAuthCallback(this)
                .build();
        dialog.verifyPhoneNumber(phoneNo);
        dialog.show();
        showDarkBack();
    }

    private void successAuth(@NonNull AuthResultModel data) {
        new Handler().post(() -> {
            String authToken = data.getToken();
            Account account = new Account(getResources().getString(R.string.app_name), Constants.ACCOUNT_TYPE);
            AccountManager accountManager = AccountManager.get(this);

            Bundle userData = new Bundle();
            userData.putLong(Constants.ACCOUNT_UID, data.getUserId());
            accountManager.addAccountExplicitly(account, null, userData);
            accountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, authToken);

            startService(new Intent(getApplicationContext(), DeviceTokenService.class));

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onSuccessVerification(DialogInterface dialog, PhoneAuthCredential credential) {
        authenticateWithCredential(credential);
        dialog.dismiss();
    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {
        hideDarkBack();
    }

    @Override
    public void onFailedVerification(DialogInterface dialog, FirebaseException exception) {
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            hideDarkBack();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_AUTH_GOOGLE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    mAuthViewModel.setProviderIdToken(account.getIdToken());
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    authenticateWithCredential(credential);
                } catch (ApiException e) {
                    Log.e(TAG, "GoogleAuth - Verification Failed: ", e);

                    hideProgress();
                    Toast.makeText(getApplicationContext(), R.string.error_authentication_google, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
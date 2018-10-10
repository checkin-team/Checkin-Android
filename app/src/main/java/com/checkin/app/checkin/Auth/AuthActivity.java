package com.checkin.app.checkin.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity implements AuthFragmentInteraction {
    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int RC_AUTH_GOOGLE = 1000;

    @BindView(R.id.circle_progress) ProgressBar vCircleProgress;
    @BindView(R.id.dark_back) View mDarkBack;

    private CallbackManager mFacebookCallbackManager;
    private PhoneAuth mPhoneAuth;
    private FirebaseAuth mAuth;
    private AuthViewModel mAuthViewModel;
    private boolean goBack = true;

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
            Log.e(TAG, "User already exists.");
            user.delete();
        }

        mPhoneAuth = new PhoneAuth(mAuth) {
            @Override
            protected void onVerificationSuccess(PhoneAuthCredential credential) {
                authenticateWithCredential(credential);
            }

            @Override
            protected void onVerificationError(FirebaseException e) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e);
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(getApplicationContext(), R.string.error_unavailable_network, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_authentication_phone, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onOtpRetrievalTimedOut() {
                mAuthViewModel.setOtpTimeout(0L);
            }
        };

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);

        mAuthViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                hideProgress();
                successAuth(resource.data);
            } else if (resource.status == Resource.Status.ERROR_INVALID_REQUEST) {
                JsonNode error = resource.getErrorBody();
                if (error != null) {
                    mAuthViewModel.showError(error);
                } else {
                    Toast.makeText(getApplicationContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            } else if (resource.status == Resource.Status.LOADING) {
                showProgress();
            }
        });

        mDarkBack.setOnTouchListener((v, event) -> true);
    }

    private void showProgress() {
        vCircleProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        vCircleProgress.setVisibility(View.GONE);
    }

    private void verifyPhoneNumber() {
        mAuthViewModel.setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNo(mAuthViewModel.getPhoneNo(), this);
    }

    private void authenticateWithCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(tokenResult -> {
                            mAuthViewModel.setFireBaseIdToken(tokenResult.getToken());
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew)
                                onVerifiedNewUser();
                            else
                                onVerifiedExistingUser();
                        });
                    }
                    else {
                        Log.e(TAG, "Authentication failed", task.getException());
                        Toast.makeText(getApplicationContext(),R.string.error_authentication,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onVerifiedExistingUser() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getDisplayName() != null) {
            Toast.makeText(getApplicationContext(), "Welcome back,  " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Welcome back!", Toast.LENGTH_LONG).show();
        }
        startActivity(new Intent(this, HomeActivity.class));
        mAuthViewModel.login();
    }

    private void onVerifiedNewUser() {
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
        startActivity(new Intent(this, HomeActivity.class));
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

    @Override
    public void onResendOtpRequest() {
        verifyPhoneNumber();
    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        Log.e(TAG, "OTP: " + otp);
        authenticateWithCredential(mPhoneAuth.verifyOtp(otp));
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String userName, GENDER gender) {
        Log.e(TAG, "Username: "+ userName +"First name: " + firstName + " | Last name: " + lastName + " | Gender: " + gender.name());
        mAuthViewModel.register(firstName, lastName, gender, userName);
    }

    @Override
    public void onGoogleAuth() {
        Log.e(TAG, "GoogleAuth");
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
        Log.e(TAG, "FacebookAuth: " + loginResult);
        showProgress();

        AccessToken accessToken = loginResult.getAccessToken();

        mAuthViewModel.setProviderIdToken(accessToken.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        authenticateWithCredential(credential);
    }

    @Override
    public void onPhoneAuth(String phoneNo) {
        Log.e(TAG, "Phone number: " + phoneNo);
        mAuthViewModel.setPhoneNo(phoneNo);
        verifyPhoneNumber();
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance(this);
        otpVerificationFragment.setEnterTransition(new Fade().setDuration(5));
        replaceFragmentContainer(otpVerificationFragment);
        showDarkBack();
    }

    private void successAuth(@NonNull ObjectNode data) {
        new Handler().post(() -> {
            TestDb.populateWithTestData(getApplicationContext());

            if (!data.has("token")) {
                Log.e(TAG, "'token' field missing from the response!");
                Toast.makeText(getApplicationContext(), R.string.error_api_invalid_response, Toast.LENGTH_SHORT).show();
                return;
            }
            String authToken = data.get("token").asText();
            Account account = new Account(getResources().getString(R.string.app_name), Constants.ACCOUNT_TYPE);
            AccountManager accountManager = AccountManager.get(this);

            Bundle userData = new Bundle();
            if (data.has("account_pk"))
                userData.putString(Constants.ACCOUNT_UID, data.get("account_pk").asText());
            accountManager.addAccountExplicitly(account, null, userData);
            accountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, authToken);

            startService(new Intent(getApplicationContext(), DeviceTokenService.class));

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
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
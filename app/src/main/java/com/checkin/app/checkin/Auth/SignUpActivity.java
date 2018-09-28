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
import android.support.constraint.ConstraintLayout;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;
import com.checkin.app.checkin.Utility.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements SignUpFragmentInteraction  {
    private String TAG = SignUpActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    private SharedPreferences mPrefs;
    private boolean goBack = true;
    private static final int PHONE_LOGIN_REQUEST_CODE = 100;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private FirebaseAuth mAuth;
    String Provider_token="";
    String id_token;
    private PhoneAuthProvider mPhoneAuth;
    private String mVerificationId;
    private AuthViewModel mAuthViewModel;

    @BindView(R.id.Login_layout)
    ConstraintLayout constraintLayout;

    @BindView(R.id.Layout_line)
    LinearLayout linearLayout;
    private GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 1000;

    @BindView(R.id.dark_back)
    View mDarkBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        callbackManager = CallbackManager.Factory.create();

        LoginButton btnLoginFb = findViewById(R.id.btn_login_fb);
        btnLoginFb.setReadPermissions(Arrays.asList("email", "user_friends"));

        btnLoginFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                Log.e("Access", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, SignupPhoneFragment.newInstance(this));
            transaction.commit();
        }

        mAuth = FirebaseAuth.getInstance();
        mPhoneAuth = PhoneAuthProvider.getInstance(mAuth);
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "onVerificationFailed", e);
                Toast.makeText(getApplicationContext(), "Verification Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                SignUpActivity.this.mVerificationId = verificationId;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                super.onCodeAutoRetrievalTimeOut(verificationId);
                mAuthViewModel.setOtpTimeout(0);
            }
        };

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);

        mAuthViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource != null && objectNodeResource.status == Resource.Status.SUCCESS) {
                successAuth(objectNodeResource.data);
            }
        });

        mDarkBack.setOnTouchListener((v, event) -> true);
    }

    private void verifyPhoneNumber() {
        mAuthViewModel.setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNumber(
                mAuthViewModel.getPhoneNo(),
                Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT,
                TimeUnit.MILLISECONDS,
                this,
                mCallBack
        );
    }

    private void signInWithAuthCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                            @Override
                            public void onSuccess(GetTokenResult getTokenResult) {
                                Log.e(TAG, "FirebaseIdToken: " + getTokenResult.getToken());
                                id_token=getTokenResult.getToken();

                                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                if(!isNew)
                                    AlreadyLoggedinUser();
                                else
                                    NewUserSignUp();
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error in Signing Up",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AlreadyLoggedinUser() {
        Toast.makeText(getApplicationContext(), "Welcome back  " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        mAuthViewModel.login(id_token,Provider_token);
    }

    private void NewUserSignUp()
    {
        Log.e(TAG, "Verified!");

        FirebaseUser user = mAuth.getCurrentUser();
        String name = user.getDisplayName();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        Fragment fragment = SignupUserInfoFragment.newInstance(SignUpActivity.this);
        fragment.setArguments(bundle);
        goBack = false;
        constraintLayout.setVisibility(ConstraintLayout.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        linearLayout.setVisibility(LinearLayout.GONE);
        replaceFragmentContainer(fragment);
    }

    private void replaceFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPhoneNumberProcess(String phoneNo) {
        Log.e(TAG, "Phone number: " + phoneNo);
        mAuthViewModel.setPhoneNo(phoneNo);
        verifyPhoneNumber();
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance(this);
        Fade fade = new Fade();
        fade.setDuration(5);
        otpVerificationFragment.setEnterTransition(fade);
        replaceFragmentContainer(otpVerificationFragment);
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkBack.setVisibility(View.VISIBLE);
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
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithAuthCredential(credential);
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String userName, GENDER gender) {
        Log.e(TAG, "Username: "+ userName +"First name: " + firstName + " | Last name: " + lastName + " | Gender: " + gender.name());
        // Post to SERVER
        mAuthViewModel.register(id_token,Provider_token,firstName,lastName,gender,userName);

    }

    private void successAuth(ObjectNode data) {
        new Handler().post(() -> {
            // TODO: Test DB population
            TestDb.populateWithTestData(getApplicationContext());

            if (data != null) {

                Account account = new Account(getResources().getString(R.string.app_name), Constants.ACCOUNT_TYPE);
                AccountManager accountManager = AccountManager.get(this);

                accountManager.addAccountExplicitly(account, null, null);
                accountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, data.get("token").asText());

           }
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Constants.SP_LOGIN_TOKEN, data != null ? data.get("token").asText() : "false");
            editor.putBoolean(Constants.SP_LOGGED_IN, true);
            editor.apply();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onSignInClicked() {
        startActivityForResult(new Intent(this, LoginActivity.class), PHONE_LOGIN_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            mDarkBack.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDarkBack.setVisibility(View.GONE);
                        }
                    });
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHONE_LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.SP_LOGGED_IN, false)) {
                    finish();
                }
                break;
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String authID=account.getId();

                    Log.e(TAG, "GoogleUID:" + authID);

                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    Log.e(TAG,"GoogleIdToken: "+account.getIdToken());
                    Provider_token=account.getIdToken();

                    signInWithAuthCredential(credential);

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(TAG, "Google sign in failed", e);
                }
                break;

            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_login_google)
    public void onGoogleLogin(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        findViewById(R.id.loadingPanel).setVisibility(RelativeLayout.VISIBLE);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "FacebookAccessToken: " + token.getToken());
        Provider_token=token.getToken();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithAuthCredential(credential);
    }
}
package com.checkin.app.checkin.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.checkin.app.checkin.Auth.LoginActivity;
import com.checkin.app.checkin.Auth.OtpVerificationFragment;
import com.checkin.app.checkin.Auth.SignUpFragmentInteraction;
import com.checkin.app.checkin.Auth.SignupPhoneFragment;
import com.checkin.app.checkin.Auth.SignupUserInfoFragment;
import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements SignUpFragmentInteraction {
    private String TAG = SignupActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    private SharedPreferences mPrefs;
    private boolean goBack = true;
    private static final int PHONE_LOGIN_REQUEST_CODE = 100;
    @BindView(R.id.dark_back) View mDarkBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initiateSignUp();
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, SignupPhoneFragment.newInstance(this));
            transaction.commit();
        }
        TestDb.populateWithTestData(getApplicationContext());

        //fb login
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(SignupActivity.this,HomeActivity.class));
                        finish();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignupActivity.this, "Facebook Login Cancelled", Toast.LENGTH_SHORT).show();
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SignupActivity.this, "Facebook Login Error", Toast.LENGTH_LONG).show();
                        // App code
                    }
                });

        //twitter login
        client = new TwitterAuthClient();


    }

    private void replaceFragmentContainer(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPhoneNumberProcess(String phoneNo) {
        // TODO: Process phone no.
        Log.e(TAG, "Phone No: " + phoneNo);
        OtpVerificationFragment otpVerificationFragment= OtpVerificationFragment.newInstance(this);
        Fade fade=new Fade();
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

    }

    @Override
    public void onOtpVerificationProcess(String otp) {
        Log.e(TAG, "OTP: " + otp);
        goBack = false;
        replaceFragmentContainer(SignupUserInfoFragment.newInstance(this));
    }

    @Override
    public void onUserInfoProcess(String firstName, String lastName, String password, UserModel.GENDER gender) {
        Log.e(TAG, "First name: " + firstName + " | Last name: " + lastName + " | Password: " + password + " | Gender: " + gender.name());
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(Constants.SP_USER_ID, 0);
        editor.putBoolean(Constants.SP_LOGGED_IN, true);
        editor.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onSignInClicked() {
        startActivityForResult(new Intent(this, LoginActivity.class), PHONE_LOGIN_REQUEST_CODE);

    }

    public void initiateSignUp()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Twitter.initialize(this);

    }

    public void socialSignIn(View v){
        switch (v.getId())
        {
            case R.id.btn_connect_google:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.btn_connect_facebook:
                Log.e(TAG, "socialSignIn: " );
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile", "user_friends"));
                 fbsignup=true;
                 break;
            case R.id.btn_connect_twitter:

                Log.e(TAG, "socialSignIn: " );
                //make the call to login
                client.authorize(this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        startActivity(new Intent(SignupActivity.this,HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        //feedback
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
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
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.SP_LOGGED_IN,false)){
                    finish();
                }
                break;
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            default:
                break;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Sign up Successful.You still need to enter your phone number.", Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            //Give data to server
            //startActivity(new Intent(this,HomeActivity.class));
            //finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Sign In by Google Failed", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private  CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private TwitterAuthClient client;
    private final int RC_SIGN_IN=3;
    private boolean fbsignup=false;

}

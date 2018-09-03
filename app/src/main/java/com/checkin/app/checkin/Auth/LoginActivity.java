package com.checkin.app.checkin.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionListenerAdapter;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.TestDb;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by TAIYAB on 03-06-2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_root) ViewGroup rootView;
    @BindView(R.id.btn_signin) ConstraintLayout btnSignIn;
    @BindView(R.id.im_signin) View imSignIn;
    @BindView(R.id.tv_signin) TextView tvSignIn;
    @BindView(R.id.space_below) Space spaceBelow;
    @BindView(R.id.space_above) Space spaceAbove;
    @BindView(R.id.login_username) EditText etUsername;
    @BindView(R.id.login_password) EditText etPassword;

    private SharedPreferences mPrefs;
    private AuthViewModel mAuthViewModel;
    private static boolean populated = false;
    private boolean doTransition = false;
    private boolean loginSuccess = false;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        btnSignIn.setOnClickListener(this);

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);
        mAuthViewModel.getObservableData().observe(this, result -> {
            if (result != null) login(result);
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.isEmpty())
                    etUsername.setError("Username can't be empty.");
                else if (password.isEmpty())
                    etPassword.setError("Password can't be empty.");
                else {
                    if (doTransition || loginSuccess) return;
                    mAuthViewModel.login(username, password);
                }
                break;
            case R.id.text_forgot:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void successLogin(ObjectNode data) {
        loginSuccess = true;
        onAnimationFinish();

        new Handler().post(() -> {
            // TODO: Test DB population
            if (!populated)
                TestDb.populateWithTestData(getApplicationContext());
            populated = true;

            if (data != null) {
                Account account = new Account(data.get("user_id").asText(), Constants.ACCOUNT_TYPE);
                AccountManager accountManager = AccountManager.get(this);

                accountManager.addAccountExplicitly(account, null, null);
                accountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, data.get("token").asText());
            }
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Constants.SP_USER_ID, data != null ? data.get("user_id").asText() : "0");
            editor.putString(Constants.SP_LOGIN_TOKEN, data != null ? data.get("token").asText() : "false");
            editor.putBoolean(Constants.SP_LOGGED_IN, true);
            editor.apply();
        });

        new Handler().postDelayed(this::launchHomeActivity, 2000);
    }

    private void failureLogin() {
        onAnimationCancel();
    }

    private void animateSignIn() {
        final LinearLayout.LayoutParams btnSignInLayoutParams = (LinearLayout.LayoutParams) btnSignIn.getLayoutParams();

        LinearLayout.LayoutParams spaceAboveLayoutParams = (LinearLayout.LayoutParams) spaceAbove.getLayoutParams();
        LinearLayout.LayoutParams spaceBelowLayoutParams = (LinearLayout.LayoutParams) spaceBelow.getLayoutParams();

        final AutoTransition transition0 = new AutoTransition();
        final AutoTransition transition1 = new AutoTransition();
        transition0.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                if (!doTransition) {
                    Log.e("SignIn Animation", "doTransition: false" );
                    return;
                }
                TransitionManager.beginDelayedTransition(rootView, transition1);
                spaceAboveLayoutParams.weight = .06f;
                spaceBelowLayoutParams.weight = .02f;
                spaceAbove.setLayoutParams(spaceAboveLayoutParams);
                spaceBelow.setLayoutParams(spaceBelowLayoutParams);
            }
        });

        transition1.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                if (!doTransition)
                    return;
                TransitionManager.beginDelayedTransition(rootView, transition0);
                spaceAboveLayoutParams.weight = .01f;
                spaceBelowLayoutParams.weight = .07f;
                spaceAbove.setLayoutParams(spaceAboveLayoutParams);
                spaceBelow.setLayoutParams(spaceBelowLayoutParams);
            }
        });


        TransitionManager.beginDelayedTransition(rootView, transition0);

        spaceAboveLayoutParams.weight = .01f;
        spaceBelowLayoutParams.weight += .02f;
        spaceAbove.setLayoutParams(spaceAboveLayoutParams);
        spaceBelow.setLayoutParams(spaceBelowLayoutParams);
        btnSignInLayoutParams.width = (int) (btnSignIn.getWidth() * .18f);
        tvSignIn.setText("···");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tvSignIn.setLetterSpacing(-.65f);
        }
        tvSignIn.setTypeface(tvSignIn.getTypeface(), Typeface.BOLD);
        tvSignIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        btnSignIn.setLayoutParams(btnSignInLayoutParams);
    }

    private void launchHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        Intent intent = new Intent();
        intent.putExtra(Constants.SP_LOGGED_IN, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void login(Resource<ObjectNode> result) {
        switch (result.status) {
            case SUCCESS: {
                successLogin(result.data);
                break;
            }
            case LOADING: {
                Log.e(TAG, "LOADING");
                doTransition = true;
                animateSignIn();
                break;
            }
            case ERROR_INVALID_REQUEST: {
                // Inform user of Invalid credentials!
            }
            default: {
                // TODO: DEBUG mode
//                successLogin(null);
                failureLogin();
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                Log.e(result.status.name(), result.message == null ? "Null" : result.message);
                break;
            }
        }
    }

    private void onAnimationFinish() {
        Log.e(TAG, "Animation finished.");
        new Handler().postDelayed(() -> {
            doTransition = false;
            TransitionManager.endTransitions(rootView);
            TransitionManager.beginDelayedTransition(rootView, new AutoTransition());
            btnSignIn.setBackgroundResource(R.color.color_primary_red);
            tvSignIn.setText("Login Successful!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tvSignIn.setLetterSpacing(0f);
            }
            btnSignIn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }, 1500);
    }

    private void onAnimationCancel() {
        Log.e(TAG, "Animation cancelled.");
        new Handler().postDelayed(() -> {
            doTransition = false;
            TransitionManager.endTransitions(rootView);
            TransitionManager.beginDelayedTransition(rootView, new AutoTransition());
            btnSignIn.setBackgroundResource(R.color.transparent);
            tvSignIn.setText("Sign in");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tvSignIn.setLetterSpacing(0f);
            }
            tvSignIn.setTypeface(tvSignIn.getTypeface(), Typeface.NORMAL);
            tvSignIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            LinearLayout.LayoutParams params = ((LinearLayout.LayoutParams) btnSignIn.getLayoutParams());
            params.width = Util.dpToPx(309);
            params.height = 0;
            btnSignIn.setLayoutParams(params);
            LinearLayout.LayoutParams belowParams = ((LinearLayout.LayoutParams) spaceBelow.getLayoutParams());
            belowParams.weight = .05f;
            LinearLayout.LayoutParams aboveParams = ((LinearLayout.LayoutParams) spaceAbove.getLayoutParams());
            aboveParams.weight = .03f;
            spaceAbove.setLayoutParams(aboveParams);
            spaceBelow.setLayoutParams(belowParams);
        }, 1000);
    }

    @OnClick(R.id.tv_register)
    public void onRegisterClicked() {
        finish();
    }
}

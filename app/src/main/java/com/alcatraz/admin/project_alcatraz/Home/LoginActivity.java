package com.alcatraz.admin.project_alcatraz.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.Auth.AuthViewModel;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.TestDb;
import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by TAIYAB on 03-06-2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btn_signin) FrameLayout btnSignIn;
    @BindView(R.id.im_signin) ImageView imSignIn;
    @BindView(R.id.tv_signin) TextView tvSignIn;
    @BindView(R.id.login_username) EditText etUsername;
    @BindView(R.id.login_password) EditText etPassword;

    private SharedPreferences mPrefs;
    private AuthViewModel mAuthViewModel;
    private static boolean populated = false;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        FrameLayout btnSignIn = findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(this);

        mAuthViewModel = ViewModelProviders.of(this, new AuthViewModel.Factory(getApplication())).get(AuthViewModel.class);
        mAuthViewModel.getObservableData().observe(this, result -> {
            if (result != null) login(result);
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPrefs.getBoolean(Constants.SP_LOGGED_IN, false)) {
            launchHomeActivity();
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
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
                    mAuthViewModel.login(username, password);
                }
                break;
            case R.id.text_forgot:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void successLogin(Map<String, String> data) {
        // TODO: Test DB population
        if (!populated)
            TestDb.populateWithTestData(getApplicationContext());
        populated = true;

        Log.e(TAG, new Gson().toJson(data));
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(Constants.SP_USER_ID, 0);
        editor.putBoolean(Constants.SP_LOGGED_IN, true);
        editor.apply();
        imSignIn.animate().scaleX(.25f).setDuration(2000L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tvSignIn.setText("...");
                TranslateAnimation anim = new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE, 0f,
                        TranslateAnimation.ABSOLUTE, 0f,
                        TranslateAnimation.RELATIVE_TO_SELF, -.4f,
                        TranslateAnimation.RELATIVE_TO_SELF, .5f
                );
                anim.setDuration(1000L);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(3);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        launchHomeActivity();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                btnSignIn.setAnimation(anim);
            }
        });
    }

    private void launchHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        Intent intent = new Intent();
        intent.putExtra(Constants.SP_LOGGED_IN, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void login(Resource<Map<String, String>> result) {
        switch (result.status) {
            case SUCCESS: {
                successLogin(result.data);
            }
            case ERROR_INVALID_REQUEST: {
                // Inform user of Invalid credentials!
            }
            default: {
                // TODO: DEBUG mode
                successLogin(new HashMap<>());
                Log.e(result.status.name(), result.message == null ? "Null" : result.message);
            }
        }
    }

    @OnClick(R.id.tv_register)
    public void onRegisterClicked() {
        finish();
    }
}

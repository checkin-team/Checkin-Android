package com.alcatraz.admin.project_alcatraz.Home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alcatraz.admin.project_alcatraz.Auth.AuthViewModel;
import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.AppDatabase;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.TestDb;
import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import proguard.annotation.BuildConfig;

/**
 * Created by TAIYAB on 03-06-2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences mPrefs;
    private AuthViewModel mAuthViewModel;
    private static boolean populated = false;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton btnSignIn = findViewById(R.id.btn_signin);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                EditText et_username = findViewById(R.id.login_username);
                EditText et_password = findViewById(R.id.login_password);
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                if (username.isEmpty())
                    et_username.setError("Username can't be empty.");
                else if (password.isEmpty())
                    et_password.setError("Password can't be empty.");
                else {
                    mAuthViewModel.login(username, password);
                }
                break;
            case R.id.text_forgot:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void successLogin(Map<String, String> data) {
        if (!populated)
            TestDb.populateWithTestData(getApplicationContext());
        populated = true;
        Log.e(TAG, new Gson().toJson(data));
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(Constants.SP_USER_ID, 0);
        editor.putBoolean(Constants.SP_LOGGED_IN, true);
        editor.apply();
        launchHomeActivity();
    }

    private void launchHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
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
}

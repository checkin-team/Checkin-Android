package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;

/**
 * Created by TAIYAB on 03-06-2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences mPrefs;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton btnSignIn = findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(this);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent;
        if (mPrefs.getBoolean(Constants.SP_LOGGED_IN, false)) {
            intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
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
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(Constants.SP_USERNAME, username);
                    editor.putBoolean(Constants.SP_LOGGED_IN, true);
                    editor.putString(Constants.SP_LOGIN_TOKEN, login());
                    editor.apply();
                    startActivity(new Intent(this, HomeActivity.class));
                }
        }
    }

    private String login() {
        //TODO: Implement login and return login token.
        return null;
    }
}
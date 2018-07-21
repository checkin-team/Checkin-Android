package com.alcatraz.admin.project_alcatraz.Home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
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

        ApiClient.getClient(this);

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
                EditText et_username = findViewById(R.id.login_username);
                EditText et_password = findViewById(R.id.login_password);
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                if (username.isEmpty())
                    et_username.setError("Username can't be empty.");
                else if (password.isEmpty())
                    et_password.setError("Password can't be empty.");
                else {
                    /* TODO: Implement login functionality and save userID
                     * (or maybe let the logged-in-user ID remain 0?
                     *  => Backend gets the user info from the Request Header Token.);
                     */
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(Constants.SP_USERNAME, username);
                    editor.putInt(Constants.SP_USER_ID, 0);
                    editor.putBoolean(Constants.SP_LOGGED_IN, true);
                    editor.putString(Constants.SP_LOGIN_TOKEN, login());
                    editor.apply();
                    launchHomeActivity();
                }
                break;
            case R.id.text_forgot:
                startActivity(new Intent(this, ForgotPassword.class));
        }
    }

    private void launchHomeActivity() {
        ImageButton imageButton=findViewById(R.id.btn_signin);
        int width=imageButton.getWidth();
        /*imageButton.animate().scaleX(.5f).setDuration(5000).withEndAction(new Runnable() {
            @Override
            public void run() {
                imageButton.setScaleX(1);
                imageButton.setImageResource(R.drawable.sign_in_button);

                int req = imageButton.getWidth();
                //imageButton.getLayoutParams().width=(int)(width*.6);

                imageButton.animate().translationYBy(-100).setDuration(100).withEndAction(() -> imageButton.animate().translationYBy(200).setDuration(200).withEndAction(() -> imageButton.animate().translationYBy(-200).setDuration(200).withEndAction(() -> imageButton.animate().translationYBy(200).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }
                }).start()).start()).start()).start();
            }
        }).start();*/

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();




        //imageButton.animate().translationYBy(200).setDuration(2000).start();

        //startActivity(new Intent(this, HomeActivity.class));
        //finish();
    }

    private String login() {
        //TODO: Implement login and return login token.
        return "a2342f2vvj6unhg:vf??12";
    }
}

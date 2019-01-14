package com.checkin.app.checkin.Auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AuthOptionsFragment extends Fragment {
    private static final String TAG = AuthOptionsFragment.class.getSimpleName();

    @BindView(R.id.ed_phone) EditText edPhone;
    @BindView(R.id.btn_login_fb) LoginButton btnLoginFb;

    private Unbinder unbinder;
    private AuthFragmentInteraction mInteractionListener;
    private CallbackManager mFacebookCallbackManager;

    public AuthOptionsFragment() {}

    public static AuthOptionsFragment newInstance(AuthFragmentInteraction fragmentInteraction, CallbackManager callbackManager) {
        AuthOptionsFragment instance = new AuthOptionsFragment();
        instance.mInteractionListener = fragmentInteraction;
        instance.mFacebookCallbackManager = callbackManager;
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_auth_options, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        if (mFacebookCallbackManager != null) {
            btnLoginFb.setReadPermissions(Arrays.asList("email", "user_friends"));
            btnLoginFb.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    mInteractionListener.onFacebookAuth(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.v(TAG, "Facebook login cancelled.");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, "FacebookAuth - Verification Failed: ", error);
                    Toast.makeText(getContext(), R.string.error_authentication_facebook, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            rootView.findViewById(R.id.container_alternative_options).setVisibility(View.GONE);
        }

        return rootView;
    }

    @OnClick(R.id.btn_enter)
    public void onEnterClicked() {
        String value = edPhone.getText().toString();
        if (value.isEmpty()) {
            edPhone.setError("This field cannot be empty");
            return;
        }
        if (value.length() <= 12) {
            edPhone.setError("Invalid phone number.");
            return;
        }
        if (isNetworkUnavailable())
            return;
        mInteractionListener.onPhoneAuth(value);
    }

    @OnClick(R.id.btn_login_google)
    public void onGoogleLogin() {
        if (isNetworkUnavailable())
            return;
        mInteractionListener.onGoogleAuth();
    }

    private boolean isNetworkUnavailable() {
        if (getContext() != null && !Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), R.string.error_unavailable_network, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

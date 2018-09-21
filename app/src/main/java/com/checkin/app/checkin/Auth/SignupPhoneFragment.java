package com.checkin.app.checkin.Auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SignupPhoneFragment extends Fragment {
    @BindView(R.id.ed_phone)
    EditText edPhone;

    private Unbinder unbinder;
    private SignUpFragmentInteraction fragmentInteractionListener;

    public SignupPhoneFragment() {}

    public static SignupPhoneFragment newInstance(SignUpFragmentInteraction fragmentInteraction) {
        SignupPhoneFragment instance = new SignupPhoneFragment();
        instance.fragmentInteractionListener = fragmentInteraction;
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_signup_phone, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.im_signup)
    public void onSignUpClicked() {
        String value = edPhone.getText().toString();
        if (value.isEmpty()) {
            edPhone.setError("This field cannot be empty");
            return;
        }
        if (value.length() <= 12) {
            edPhone.setError("Invalid phone number.");
            return;
        }
        fragmentInteractionListener.onPhoneNumberProcess(value);
    }

    @OnClick(R.id.tv_signin)
    public void onSigninClicked(View v) {
        fragmentInteractionListener.onSignInClicked();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

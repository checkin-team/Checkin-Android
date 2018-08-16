package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alcatraz.admin.project_alcatraz.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SignupPhoneFragment extends Fragment {
    @BindView(R.id.ed_phone)
    EditText edPhone;

    private Unbinder unbinder;
    private SignupFragmentInteraction fragmentInteractionListener;

    public SignupPhoneFragment() {}

    public static SignupPhoneFragment newInstance(SignupFragmentInteraction fragmentInteraction) {
        SignupPhoneFragment instance = new SignupPhoneFragment();
        instance.fragmentInteractionListener = fragmentInteraction;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_signup_phone, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.im_signup)
    public void onSignupClicked(View v) {
        String value = edPhone.getText().toString();
        if (value.isEmpty()) {
            edPhone.setError("This field cannot be empty");
            return;
        }
        fragmentInteractionListener.onPhoneNumberProcess(value);
    }

    @OnClick(R.id.tv_signin)
    public void onSigninClicked(View v) {
        fragmentInteractionListener.onSigninClicked();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

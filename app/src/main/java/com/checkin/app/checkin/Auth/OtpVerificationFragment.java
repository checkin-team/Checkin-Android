package com.checkin.app.checkin.Auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.checkin.app.checkin.R;
import com.google.firebase.auth.PhoneAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OtpVerificationFragment extends Fragment {
    private SignupFragmentInteraction fragmentInteraction;
    Unbinder unbinder;
    @BindView(R.id.ed_otp) EditText edOtp;
    //@BindView(R.id.dark_back) View mDarkBack;
    public OtpVerificationFragment() {}

    public static OtpVerificationFragment newInstance(SignupActivity fragmentInteraction) {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        fragment.fragmentInteraction = (SignupFragmentInteraction) fragmentInteraction;
        return fragment;
    }

    @OnClick(R.id.btn_proceed)
    public void onProceedClick(View v) {
        String value = edOtp.getText().toString();
        if(value.isEmpty()){
            edOtp.setError("This field cannot be empty");
            return;
        }
        fragmentInteraction.onOtpVerificationProcess(value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
//        edOtp = view.findViewById(R.id.ed_otp);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unbinder.unbind();
    }
}
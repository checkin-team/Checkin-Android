package com.checkin.app.checkin.Auth;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OtpVerificationFragment extends Fragment {
    private SignUpFragmentInteraction fragmentInteraction;
    private Unbinder unbinder;
    private AuthViewModel mAuthViewModel;
    @BindView(R.id.ed_otp) EditText edOtp;
    @BindView(R.id.tv_remaining_time) TextView tvRemainingTime;
    @BindView(R.id.btn_resend_otp) Button btnResendOtp;
    public OtpVerificationFragment() {}

    public static OtpVerificationFragment newInstance(SignUpActivity fragmentInteraction) {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        fragment.fragmentInteraction = fragmentInteraction;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getActivity() == null)
            return null;

        mAuthViewModel = ViewModelProviders.of(getActivity(), new AuthViewModel.Factory(getActivity().getApplication())).get(AuthViewModel.class);
        mAuthViewModel.getOtpTimeOut().observe(this, time -> {
            long min = time/1000/60;
            long sec = time/1000-min*60;
            if (time > 0L) {
                tvRemainingTime.setText(Util.formatTime(min, sec));
            } else {
                tvRemainingTime.setVisibility(View.INVISIBLE);
                btnResendOtp.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @OnClick(R.id.btn_proceed)
    public void onProceedClick() {
        String value = edOtp.getText().toString();
        if (value.isEmpty()) {
            edOtp.setError("This field cannot be empty");
            return;
        }
        fragmentInteraction.onOtpVerificationProcess(value);
    }

    @OnClick(R.id.btn_resend_otp)
    public void onResendOtpClick() {
        fragmentInteraction.onResendOtpRequest();
        tvRemainingTime.setVisibility(View.VISIBLE);
        btnResendOtp.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
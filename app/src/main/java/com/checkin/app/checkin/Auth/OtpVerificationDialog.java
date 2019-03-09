package com.checkin.app.checkin.Auth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OtpVerificationDialog extends AlertDialog {
    private static final String TAG = OtpVerificationDialog.class.getSimpleName();

    @BindView(R.id.ed_otp)
    EditText edOtp;
    @BindView(R.id.tv_remaining_time)
    TextView tvRemainingTime;
    @BindView(R.id.btn_resend_otp)
    Button btnResendOtp;

    private Activity mActivity;
    private PhoneAuth mPhoneAuth;
    private AuthCallback mListener;
    private FirebaseAuth mAuth;

    private String mPhoneNo;

    private final AuthCallback defaultAuthCallback = new AuthCallback() {
        @Override
        public void onSuccessVerification(DialogInterface dialog, PhoneAuthCredential credential) {
            dialog.dismiss();
        }

        @Override
        public void onCancelVerification(DialogInterface dialog) {
        }

        @Override
        public void onFailedVerification(DialogInterface dialog, FirebaseException exception) {
            dialog.dismiss();
        }
    };

    OtpVerificationDialog(@NonNull Context context, AuthCallback authCallback) {
        super(context);

        if (context instanceof Activity)
            mActivity = ((Activity) context);
        if (authCallback == null)
            mListener = defaultAuthCallback;
        else
            mListener = authCallback;
        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        ViewGroup contentView = ((ViewGroup) getLayoutInflater().inflate(R.layout.fragment_otp_verification, null));
        Unbinder unbinder = ButterKnife.bind(this, contentView);

        setView(contentView);

        mPhoneAuth = new PhoneAuth(mAuth) {
            @Override
            protected void onVerificationSuccess(PhoneAuthCredential credential) {
                mListener.onSuccessVerification(OtpVerificationDialog.this, credential);
            }

            @Override
            protected void onVerificationError(FirebaseException e) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e);
                if (e instanceof FirebaseNetworkException) {
                    Utils.toast(mActivity, R.string.error_unavailable_network);
                } else {
                    Utils.toast(mActivity, R.string.error_authentication_phone);
                }
                mListener.onFailedVerification(OtpVerificationDialog.this, e);
            }

            @Override
            protected void onOtpRetrievalTimedOut() {
                setOtpTimeout(0L);
            }
        };
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mListener.onCancelVerification(this);
    }

    @OnClick(R.id.btn_proceed)
    public void onProceedClick() {
        String value = edOtp.getText().toString();
        if (value.isEmpty()) {
            edOtp.setError("This field cannot be empty");
            return;
        }
        verifyOtp(value);
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = mPhoneAuth.verifyOtp(otp);
        if (credential == null) {
            Utils.toast(getContext(), "Verification proof not received!");
            return;
        }
        mListener.onSuccessVerification(this, credential);
    }

    @OnClick(R.id.btn_resend_otp)
    public void onResendOtpClick() {
        onResendOtpRequest();
        tvRemainingTime.setVisibility(View.VISIBLE);
        btnResendOtp.setVisibility(View.INVISIBLE);
    }

    private void onResendOtpRequest() {
        verifyPhoneNumber(mPhoneNo);
    }

    public void verifyPhoneNumber(String phone) {
        mPhoneNo = phone;
        setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT);
        mPhoneAuth.verifyPhoneNo(phone, mActivity);
    }

    private void setOtpTimeout(long timeout) {
        updateTimeout(timeout);
        if (timeout > 0) {
            new CountDownTimer(timeout, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimeout(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    updateTimeout(0L);
                }
            }.start();
        }
    }

    private void updateTimeout(long time) {
        long min = time / 1000 / 60;
        long sec = time / 1000 - min * 60;
        if (time > 0L) {
            tvRemainingTime.setText(Utils.formatTime(min, sec));
        } else {
            tvRemainingTime.setVisibility(View.INVISIBLE);
            btnResendOtp.setVisibility(View.VISIBLE);
        }
    }

    public static class Builder {
        private final Activity mActivity;
        private AuthCallback mCallback;

        protected Builder(Activity activity) {
            mActivity = activity;
        }

        public static Builder with(Activity activity) {
            return new Builder(activity);
        }

        public Builder setAuthCallback(AuthCallback callback) {
            mCallback = callback;
            return this;
        }

        public OtpVerificationDialog build() {
            return new OtpVerificationDialog(mActivity, mCallback);
        }
    }

    public interface AuthCallback {
        void onSuccessVerification(DialogInterface dialog, PhoneAuthCredential credential);

        void onCancelVerification(DialogInterface dialog);

        void onFailedVerification(DialogInterface dialog, FirebaseException exception);
    }
}

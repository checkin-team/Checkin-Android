package com.checkin.app.checkin.Auth;

import android.app.Activity;

import com.checkin.app.checkin.Utility.Constants;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public abstract class PhoneAuth extends PhoneAuthProvider.OnVerificationStateChangedCallbacks {
    private PhoneAuthProvider mPhoneAuthProvider;
    private String mVerificationId;

    protected PhoneAuth(FirebaseAuth firebaseAuth) {
        mPhoneAuthProvider = PhoneAuthProvider.getInstance(firebaseAuth);
    }

    public void verifyPhoneNo(String phoneNo, Activity activity) {
        mPhoneAuthProvider.verifyPhoneNumber(
                phoneNo,
                Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT,
                TimeUnit.MILLISECONDS,
                activity,
                this);
    }

    public PhoneAuthCredential verifyOtp(String otp) {
        return PhoneAuthProvider.getCredential(mVerificationId, otp);
    }

    @Override
    public void onVerificationCompleted(PhoneAuthCredential credential) {
        onVerificationSuccess(credential);
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        onVerificationError(e);
    }

    @Override
    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(verificationId, forceResendingToken);
        mVerificationId = verificationId;
    }

    @Override
    public void onCodeAutoRetrievalTimeOut(String verificationId) {
        super.onCodeAutoRetrievalTimeOut(verificationId);
        onOtpRetrievalTimedOut();
    }

    protected abstract void onVerificationSuccess(PhoneAuthCredential credential);
    protected abstract void onVerificationError(FirebaseException e);
    protected abstract void onOtpRetrievalTimedOut();
}

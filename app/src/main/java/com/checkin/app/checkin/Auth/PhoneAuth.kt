package com.checkin.app.checkin.Auth

import android.app.Activity
import com.checkin.app.checkin.utility.Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

abstract class PhoneAuth protected constructor(firebaseAuth: FirebaseAuth) : OnVerificationStateChangedCallbacks() {
    private val mPhoneAuthProvider = PhoneAuthProvider.getInstance(firebaseAuth)
    private var mVerificationId: String? = null

    fun verifyPhoneNo(phoneNo: String?, activity: Activity?) {
        mPhoneAuthProvider.verifyPhoneNumber(
                phoneNo!!,
                DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT,
                TimeUnit.MILLISECONDS,
                activity!!,
                this)
    }

    fun verifyOtp(otp: String): PhoneAuthCredential? = mVerificationId?.let { PhoneAuthProvider.getCredential(it, otp) }

    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        onVerificationSuccess(credential)
    }

    override fun onVerificationFailed(e: FirebaseException) {
        onVerificationError(e)
    }

    override fun onCodeSent(verificationId: String, forceResendingToken: ForceResendingToken) {
        super.onCodeSent(verificationId, forceResendingToken)
        mVerificationId = verificationId
    }

    override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
        super.onCodeAutoRetrievalTimeOut(verificationId)
        onOtpRetrievalTimedOut()
    }

    protected abstract fun onVerificationSuccess(credential: PhoneAuthCredential?)
    protected abstract fun onVerificationError(e: FirebaseException)
    protected abstract fun onOtpRetrievalTimedOut()
}
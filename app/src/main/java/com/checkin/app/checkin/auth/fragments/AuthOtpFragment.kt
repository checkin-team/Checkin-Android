package com.checkin.app.checkin.auth.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.PhoneAuth
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.checkin.app.checkin.utility.toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class AuthOtpFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_otp_verification

    val args: AuthOtpFragmentArgs by navArgs()
    private val mListener: AuthCallback by parentActivityDelegate()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mPhoneNo: String by lazy {
        args.phone
    }

    @BindView(R.id.tv_auth_otp_resend)
    internal lateinit var tvResendOtp: TextView
    @BindView(R.id.tv_auth_otp_time)
    internal lateinit var tvRemainingTime: TextView
    @BindView(R.id.otp_auth_otp_otp)
    internal lateinit var otpView: OtpView
    @BindView(R.id.tv_auth_otp_number)
    internal lateinit var otpNumber: TextView

    private val mPhoneAuth: PhoneAuth by lazy {
        object : PhoneAuth(mAuth) {
            override fun onVerificationSuccess(credential: PhoneAuthCredential) {
                authenticateCredential(credential)
            }

            override fun onVerificationError(e: FirebaseException) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e)
                val err = if (e is FirebaseNetworkException) NoConnectivityException() else e
                mListener.onFailedVerification(err)
            }

            override fun onOtpRetrievalTimedOut() {
                setOtpTimeout(0L)
            }

            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken)
                setOtpTimeout(Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val text = getString(R.string.resend_otp)
        tvResendOtp.text = Utils.fromHtml(text)

        otpNumber.text = "OTP Sent to $mPhoneNo"
        verifyPhoneNumber()
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener.onCancelVerification()
    }

    @OnClick(R.id.btn_auth_otp_verify)
    fun onProceedClick() {
        val value = otpView.otpValue
        if (value == null) {
            toast("OTP cannot be empty")
            return
        }
        verifyOtp(value)
    }

    private fun verifyOtp(otp: String) {
        val credential = mPhoneAuth.verifyOtp(otp)
        if (credential == null) {
            toast("Verification proof not received!")
            return
        }
        authenticateCredential(credential)
    }

    private fun authenticateCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            var err = it.exception
            val status = if (it.isSuccessful) mAuth.currentUser?.let {
                var result = false
                it.getIdToken(false).addOnCompleteListener { tokenResult ->
                    if (tokenResult.isSuccessful) tokenResult.result?.token?.let { token ->
                        result = true
                        mListener.onSuccessVerification(credential, token)
                    } else err = tokenResult.exception
                }
                result
            } ?: false else false
            if (!status) {
                if (err is FirebaseAuthInvalidCredentialsException)
                    err = InvalidOTPException()
                err?.let {
                    mListener.onFailedVerification(it)
                    Utils.logErrors(TAG, it, requireContext().getString(R.string.error_authentication_phone))
                }
            }
        }
    }

    @OnClick(R.id.tv_auth_otp_resend)
    fun onResendOtpClick() {
        onResendOtpRequest()
        tvRemainingTime.visibility = View.VISIBLE
    }

    private fun onResendOtpRequest() {
        verifyPhoneNumber()
    }

    private fun verifyPhoneNumber() {
        mPhoneAuth.verifyPhoneNo(mPhoneNo, requireActivity())
        setOtpTimeout(0L)
    }

    private fun setOtpTimeout(timeout: Long) {
        updateTimeout(timeout)
        if (timeout > 0L) {
            object : CountDownTimer(timeout, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    updateTimeout(millisUntilFinished)
                }

                override fun onFinish() {
                    updateTimeout(0L)
                }
            }.start()
        }
    }

    private fun updateTimeout(time: Long) {
        val min = time / 1000 / 60
        val sec = time / 1000 - min * 60
        if (time > 0L) {
            tvRemainingTime.text = Utils.formatTime(min, sec)
            tvRemainingTime.visibility = View.VISIBLE
        } else {
            tvRemainingTime.visibility = View.GONE
        }
    }

    interface AuthCallback {
        fun onSuccessVerification(credential: PhoneAuthCredential, idToken: String)
        fun onCancelVerification()
        fun onFailedVerification(exception: Exception)
    }


    companion object {
        private val TAG = AuthOtpFragment::class.java.simpleName
    }
}

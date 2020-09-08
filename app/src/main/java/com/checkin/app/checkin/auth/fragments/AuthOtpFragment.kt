package com.checkin.app.checkin.auth.fragments

import android.os.Bundle
import android.os.CountDownTimer
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
import com.checkin.app.checkin.misc.views.OtpView
import com.checkin.app.checkin.utility.*
import com.checkin.app.checkin.utility.Utils.logErrors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class AuthOtpFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_otp_verification

    @BindView(R.id.tv_auth_otp_resend)
    internal lateinit var tvResendOtp: TextView

    @BindView(R.id.tv_auth_otp_time)
    internal lateinit var tvRemainingTime: TextView

    @BindView(R.id.otp_auth_otp_otp)
    internal lateinit var otpView: OtpView

    @BindView(R.id.tv_auth_otp_number)
    internal lateinit var otpNumber: TextView

    // global variable for otpTimer in case the fragment is destroyed
    private lateinit var otpTimer: CountDownTimer

    private val mobileAuthCallBack: AuthCallback by parentActivityDelegate()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val phone: String by lazy {
        navArgs<AuthOtpFragmentArgs>().value.phone
    }

    private val phoneAuth: PhoneAuth by lazy {
        object : PhoneAuth(firebaseAuth) {
            override fun onVerificationSuccess(credential: PhoneAuthCredential) {
                authenticateCredential(credential)
            }

            override fun onVerificationError(e: FirebaseException) {
                logErrors(TAG, e, "PhoneAuth - Verification Failed: ")
                val err = if (e is FirebaseNetworkException) NoConnectivityException() else e
                mobileAuthCallBack.onFailedVerification(err)
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
        otpNumber.text = phone.removePrefix(getString(R.string.prefix_country_code))
        verifyPhoneNumber()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::otpTimer.isInitialized) {
            otpTimer.cancel()
        }
    }

    @OnClick(R.id.btn_auth_otp_verify)
    fun onProceedClick() {
        val value = otpView.otpValue
        if (value.isEmpty()) {
            toast("OTP cannot be empty")
            return
        }
        verifyOtp(value)
    }

    private fun verifyOtp(otp: String) {
        val credential = phoneAuth.verifyOtp(otp)
        if (credential == null) {
            toast("Verification proof not received!")
            return
        }
        authenticateCredential(credential)
    }

    private fun authenticateCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            var err = it.exception
            val status = if (it.isSuccessful) firebaseAuth.currentUser?.let {
                var result = false
                it.getIdToken(false).addOnCompleteListener { tokenResult ->
                    if (tokenResult.isSuccessful) tokenResult.result?.token?.let { token ->
                        result = true
                        mobileAuthCallBack.onSuccessVerification(credential, token, phone)
                    } else err = tokenResult.exception
                }
                result
            } ?: false else false
            if (!status) {
                if (err is FirebaseAuthInvalidCredentialsException)
                    err = InvalidOTPException()
                err?.let {
                    mobileAuthCallBack.onFailedVerification(it)
                    it.log(TAG, "Phone authentication failed")
                }
            }
        }
    }

    @OnClick(R.id.tv_auth_otp_resend)
    fun onResendOtpClick() {
        onResendOtpRequest()
        tvRemainingTime.visibility = View.VISIBLE
        tvResendOtp.visibility = View.GONE
    }

    private fun onResendOtpRequest() {
        verifyPhoneNumber()
    }

    private fun verifyPhoneNumber() {
        phoneAuth.verifyPhoneNo(phone, requireActivity())
        setOtpTimeout(0L)
    }

    private fun setOtpTimeout(timeout: Long) {
        updateTimeout(timeout)
        if (timeout > 0L) {
            createOtpTimer(timeout)
            otpTimer.start()
        }
    }

    private fun updateTimeout(time: Long) {
        val min = time / 1000 / 60
        val sec = time / 1000 - min * 60
        if (::tvRemainingTime.isInitialized)
            if (time > 0L) {
                tvRemainingTime.text = Utils.formatTime(min, sec)
                tvRemainingTime.visibility = View.VISIBLE
                tvResendOtp.visibility = View.GONE
            } else {
                tvRemainingTime.visibility = View.GONE
                tvResendOtp.visibility = View.VISIBLE
            }
    }

    private fun createOtpTimer(timeout: Long) {
        otpTimer = object : CountDownTimer(timeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimeout(millisUntilFinished)
            }

            override fun onFinish() {
                updateTimeout(0L)
            }
        }
    }

    interface AuthCallback {
        fun onSuccessVerification(credential: PhoneAuthCredential, idToken: String, phone: String)
        fun onCancelVerification()
        fun onFailedVerification(exception: Exception)
    }

    companion object {
        private val TAG = AuthOtpFragment::class.java.simpleName
    }
}

package com.checkin.app.checkin.auth.fragments

import android.app.Activity
import android.content.DialogInterface
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.PhoneAuth
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import com.checkin.app.checkin.utility.Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerificationDialog internal constructor(val activity: Activity, authCallback: AuthCallback?) : AlertDialog(activity) {
    @BindView(R.id.ed_otp)
    internal lateinit var edOtp: EditText
    @BindView(R.id.tv_remaining_time)
    internal lateinit var tvRemainingTime: TextView
    @BindView(R.id.btn_resend_otp)
    internal lateinit var btnResendOtp: Button

    private val mPhoneAuth: PhoneAuth by lazy {
        object : PhoneAuth(mAuth) {
            override fun onVerificationSuccess(credential: PhoneAuthCredential) {
                authenticateCredential(credential)
            }

            override fun onVerificationError(e: FirebaseException) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e)
                val err = if (e is FirebaseNetworkException) NoConnectivityException() else e
                mListener?.onFailedVerification(this@OtpVerificationDialog, err)
            }

            override fun onOtpRetrievalTimedOut() {
                setOtpTimeout(0L)
            }

            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken)
                setOtpTimeout(DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT)
            }
        }
    }
    private var mListener: AuthCallback? = null
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mPhoneNo: String? = null

    init {
        mListener = authCallback ?: defaultAuthCallback
        init()
    }

    private fun init() {
        val contentView = layoutInflater.inflate(R.layout.fragment_otp_verification, null)
        ButterKnife.bind(this, contentView)
        setView(contentView)
    }

    override fun dismiss() {
        super.dismiss()
        mListener?.onCancelVerification(this)
    }

    @OnClick(R.id.btn_proceed)
    fun onProceedClick() {
        val value = edOtp.text.toString()
        if (value.isEmpty()) {
            edOtp.error = "This field cannot be empty"
            return
        }
        verifyOtp(value)
    }

    private fun verifyOtp(otp: String) {
        val credential = mPhoneAuth.verifyOtp(otp)
        if (credential == null) {
            context.toast("Verification proof not received!")
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
                        mListener?.onSuccessVerification(this, credential, token)
                    } else err = tokenResult.exception
                }
                result
            } ?: false else false
            if (!status) {
                err?.let {
                    Utils.logErrors(TAG, it, context.getString(R.string.error_authentication_phone))
                    mListener?.onFailedVerification(this, it)
                }
            }
        }
    }

    @OnClick(R.id.btn_resend_otp)
    fun onResendOtpClick() {
        onResendOtpRequest()
        tvRemainingTime.visibility = View.VISIBLE
        btnResendOtp.visibility = View.INVISIBLE
    }

    private fun onResendOtpRequest() {
        verifyPhoneNumber(mPhoneNo)
    }

    fun verifyPhoneNumber(phone: String?) {
        mPhoneNo = phone ?: return
        mPhoneAuth.verifyPhoneNo(phone, activity)
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
            btnResendOtp.visibility = View.INVISIBLE
        } else {
            tvRemainingTime.visibility = View.INVISIBLE
            btnResendOtp.visibility = View.VISIBLE
        }
    }

    interface AuthCallback {
        fun onSuccessVerification(dialog: DialogInterface?, credential: PhoneAuthCredential, idToken: String)
        fun onCancelVerification(dialog: DialogInterface?)
        fun onFailedVerification(dialog: DialogInterface?, exception: Exception)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mListener = null
    }

    class Builder private constructor(private val mActivity: Activity) {
        private var mCallback: AuthCallback? = null
        fun setAuthCallback(callback: AuthCallback?): Builder {
            mCallback = callback
            return this
        }

        fun build(): OtpVerificationDialog {
            return OtpVerificationDialog(mActivity, mCallback)
        }

        companion object {
            @JvmStatic
            fun with(activity: Activity): Builder {
                return Builder(activity)
            }
        }
    }

    companion object {
        private val TAG = OtpVerificationDialog::class.java.simpleName

        private val defaultAuthCallback: AuthCallback = object : AuthCallback {
            override fun onSuccessVerification(dialog: DialogInterface?, credential: PhoneAuthCredential, idToken: String) {
                dialog?.dismiss()
            }

            override fun onCancelVerification(dialog: DialogInterface?) {}
            override fun onFailedVerification(dialog: DialogInterface?, exception: Exception) {
                dialog?.dismiss()
            }
        }
    }
}
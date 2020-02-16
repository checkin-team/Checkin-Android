package com.checkin.app.checkin.Auth

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
import com.checkin.app.checkin.Auth.OtpVerificationDialog
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.Constants.DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential

class OtpVerificationDialog internal constructor(val activity: Activity, authCallback: AuthCallback?) : AlertDialog(activity) {
    @BindView(R.id.ed_otp)
    internal lateinit var edOtp: EditText
    @BindView(R.id.tv_remaining_time)
    internal lateinit var tvRemainingTime: TextView
    @BindView(R.id.btn_resend_otp)
    internal lateinit var btnResendOtp: Button

    private val mPhoneAuth: PhoneAuth by lazy {
        object : PhoneAuth(mAuth) {
            override fun onVerificationSuccess(credential: PhoneAuthCredential?) {
                credential?.let {
                    mListener?.onSuccessVerification(this@OtpVerificationDialog, it)
                }
            }

            override fun onVerificationError(e: FirebaseException) {
                Log.e(TAG, "PhoneAuth - Verification Failed: ", e)
                context.toast(if (e is FirebaseNetworkException) R.string.error_unavailable_network else R.string.error_authentication_phone)
                mListener?.onFailedVerification(this@OtpVerificationDialog, e)
            }

            override fun onOtpRetrievalTimedOut() {
                setOtpTimeout(0L)
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
            Utils.toast(context, "Verification proof not received!")
            return
        }
        mListener?.onSuccessVerification(this, credential)
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
        mPhoneNo = phone
        setOtpTimeout(DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT)
        mPhoneAuth.verifyPhoneNo(phone, activity)
    }

    private fun setOtpTimeout(timeout: Long) {
        updateTimeout(timeout)
        if (timeout > 0) {
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
        } else {
            tvRemainingTime.visibility = View.INVISIBLE
            btnResendOtp.visibility = View.VISIBLE
        }
    }

    interface AuthCallback {
        fun onSuccessVerification(dialog: DialogInterface?, credential: PhoneAuthCredential)
        fun onCancelVerification(dialog: DialogInterface?)
        fun onFailedVerification(dialog: DialogInterface?, exception: FirebaseException)
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
            override fun onSuccessVerification(dialog: DialogInterface?, credential: PhoneAuthCredential) {
                dialog?.dismiss()
            }

            override fun onCancelVerification(dialog: DialogInterface?) {}
            override fun onFailedVerification(dialog: DialogInterface?, exception: FirebaseException) {
                dialog?.dismiss()
            }
        }
    }
}
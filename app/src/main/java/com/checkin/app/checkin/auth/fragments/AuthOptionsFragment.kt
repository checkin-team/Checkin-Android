package com.checkin.app.checkin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.parentActivityDelegate
import com.checkin.app.checkin.utility.pass
import com.checkin.app.checkin.utility.toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


class AuthOptionsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_options

    @BindView(R.id.ed_phone)
    internal lateinit var edPhone: EditText
    @BindView(R.id.btn_login_fb)
    internal lateinit var btnLoginFb: LoginButton
    @BindView(R.id.container_alternative_options)
    internal lateinit var containerOptions: ViewGroup

    private val mInteractionListener: AuthFragmentInteraction by parentActivityDelegate()
    private val mFacebookCallbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

    private val isNetworkUnavailable: Boolean
        get() = context?.run {
            if (!Utils.isNetworkConnected(this)) {
                toast(R.string.error_unavailable_network)
                true
            } else false
        } ?: false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLoginFb.setReadPermissions(listOf("email", "user_friends"))
        btnLoginFb.registerCallback(mFacebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                mInteractionListener.onFacebookAuth(loginResult)
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                Log.e(TAG, "FacebookAuth - Verification Failed: ", error)
                context?.toast(R.string.error_authentication_facebook)
            }
        })
    }

    @OnClick(R.id.btn_enter)
    fun onEnterClicked() {
        val value = edPhone.text.toString()
        when {
            value.isEmpty() -> {
                edPhone.error = "Phone cannot be empty"
            }
            value.length <= 12 -> {
                edPhone.error = "Invalid phone number."
            }
            isNetworkUnavailable -> pass
            else -> mInteractionListener.onPhoneAuth(value)
        }
    }

    @OnClick(R.id.btn_login_google)
    fun onGoogleLogin() {
        if (isNetworkUnavailable) return
        mInteractionListener.onGoogleAuth()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val TAG = AuthOptionsFragment::class.java.simpleName

        fun newInstance(): AuthOptionsFragment = AuthOptionsFragment()
    }
}

package com.checkin.app.checkin.Auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.ParentActivityDelegate
import com.checkin.app.checkin.Utility.Utils
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


class AuthOptionsFragment : BaseFragment() {

    @BindView(R.id.ed_phone)
    internal lateinit var edPhone: EditText
    @BindView(R.id.btn_login_fb)
    internal lateinit var btnLoginFb: LoginButton
    @BindView(R.id.container_alternative_options)
    internal lateinit var containerOptions: ViewGroup

    private val mInteractionListener: AuthFragmentInteraction by ParentActivityDelegate(this)
    private var mFacebookCallbackManager: CallbackManager? = null

    private val isNetworkUnavailable: Boolean
        get() = context?.run {
            if (!Utils.isNetworkConnected(this)) {
                Utils.toast(applicationContext, R.string.error_unavailable_network)
                true
            } else false
        } ?: false

    override val rootLayout: Int
        get() = R.layout.fragment_auth_options

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFacebookCallbackManager.let { callbackManager ->
            if (callbackManager != null) {
                btnLoginFb.setReadPermissions(listOf("email", "user_friends"))
                btnLoginFb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        mInteractionListener.onFacebookAuth(loginResult)
                    }

                    override fun onCancel() {}

                    override fun onError(error: FacebookException) {
                        Log.e(TAG, "FacebookAuth - Verification Failed: ", error)
                        context?.also { Utils.toast(it, R.string.error_authentication_facebook) }
                    }
                })
            } else containerOptions.visibility = View.GONE
        }
    }

    @OnClick(R.id.btn_enter)
    fun onEnterClicked() {
        val value = edPhone.text.toString()
        if (value.isEmpty()) {
            edPhone.error = "This field cannot be empty"
            return
        }
        if (value.length <= 12) {
            edPhone.error = "Invalid phone number."
            return
        }
        if (isNetworkUnavailable) return
        mInteractionListener.onPhoneAuth(value)
    }

    @OnClick(R.id.btn_login_google)
    fun onGoogleLogin() {
        if (isNetworkUnavailable) return
        mInteractionListener.onGoogleAuth()
    }

    companion object {
        private val TAG = AuthOptionsFragment::class.java.simpleName

        fun newInstance(callbackManager: CallbackManager): AuthOptionsFragment {
            val instance = AuthOptionsFragment()
            instance.mFacebookCallbackManager = callbackManager
            return instance
        }
    }
}

package com.checkin.app.checkin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
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
import com.google.android.material.textfield.TextInputLayout


class AuthOptionFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_option

    @BindView(R.id.btn_auth_fb)
    internal lateinit var btnLoginFb: LoginButton

    @BindView(R.id.tet_auth_contact_no)
    internal lateinit var tetContactNo: TextInputLayout

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
        btnLoginFb.apply {
            setPermissions("email", "public_profile")
            fragment = this@AuthOptionFragment
            registerCallback(mFacebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    mInteractionListener.onFacebookAuth(loginResult)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Utils.logErrors(TAG, error, "FacebookAuth - Verification Failed")
                    context?.toast(R.string.error_authentication_facebook)
                }
            })
        }

        tetContactNo.editText?.doOnTextChanged { _, _, _, _ ->
            tetContactNo.error = null
        }
        tetContactNo.setEndIconOnClickListener {
            val value = tetContactNo.editText?.text.toString()
            when {
                value.isEmpty() -> {
                    tetContactNo.error = "Phone cannot be empty"
                }
                value.length <= 12 -> {
                    tetContactNo.error = "Invalid phone number."
                }
                isNetworkUnavailable -> pass
                else -> {
                    val action = AuthOptionFragmentDirections.actionSignInFragmentToOtpFragment(value)
                    findNavController().navigate(action)
                }
            }
        }
    }

    companion object {
        private val TAG = AuthDetailsFragment::class.java.simpleName
        fun newInstance(): AuthDetailsFragment = AuthDetailsFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    @OnClick(R.id.btn_auth_google)
    fun onGoogleLogin() {
        if (isNetworkUnavailable) return
        mInteractionListener.onGoogleAuth()
    }
}

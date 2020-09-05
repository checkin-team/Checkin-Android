package com.checkin.app.checkin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
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


class AuthOptionsFragment : BaseFragment() {
    override val rootLayout: Int = R.layout.fragment_auth_option

    @BindView(R.id.btn_auth_fb)
    internal lateinit var btnLoginFb: LoginButton

    @BindView(R.id.til_auth_contact_no)
    internal lateinit var tilContactNo: TextInputLayout

    private val interaction: AuthFragmentInteraction by parentActivityDelegate()
    private val facebookCallbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

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
            fragment = this@AuthOptionsFragment
            registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    interaction.onFacebookAuth(loginResult)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Utils.logErrors(TAG, error, "FacebookAuth - Verification Failed")
                    context?.toast(R.string.error_authentication_facebook)
                }
            })
        }

        tilContactNo.setEndIconOnClickListener {
            val phone = tilContactNo.editText?.text.toString()
            when {
                phone.isEmpty() -> {
                    tilContactNo.error = "Phone cannot be empty"
                }
                phone.length <= 12 -> {
                    tilContactNo.error = "Invalid phone number."
                }
                isNetworkUnavailable -> pass
                else -> {
                    interaction.onPhoneAuth(phone)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!facebookCallbackManager.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    @OnClick(R.id.btn_auth_google)
    fun onGoogleLogin() {
        if (isNetworkUnavailable) return
        interaction.onGoogleAuth()
    }

    companion object {
        private val TAG = AuthDetailsFragment::class.java.simpleName
    }

}

package com.checkin.app.checkin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
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
                    toast(R.string.error_authentication_facebook)
                }
            })
        }

        tilContactNo.setEndIconOnClickListener {
            validatePhoneSubmit()?.also { interaction.onPhoneAuth(it) }
        }

        tilContactNo.editText?.doAfterTextChanged {
            // to reset once errored before
            if (validatePhoneSubmit() != null) tilContactNo.error = null
        }

        tilContactNo.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validatePhoneSubmit()?.also {
                    interaction.onPhoneAuth(it)
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
    }

    private fun validatePhoneSubmit(): String? {
        val phone = tilContactNo.editText?.text.toString()
        when {
            phone.isEmpty() -> {
                tilContactNo.error = "Phone cannot be empty"
            }
            phone.length <= 12 -> {
                tilContactNo.error = "Invalid phone number."
            }
            isNetworkUnavailable -> pass
            else -> return phone
        }
        return null
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

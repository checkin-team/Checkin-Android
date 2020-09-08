package com.checkin.app.checkin.auth.activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.auth.AuthResultModel
import com.checkin.app.checkin.auth.AuthViewModel
import com.checkin.app.checkin.auth.fragments.AuthOptionsFragmentDirections
import com.checkin.app.checkin.auth.fragments.AuthOtpFragment
import com.checkin.app.checkin.auth.services.DeviceTokenService
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.log
import com.checkin.app.checkin.utility.toast
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import java.util.*

class AuthActivity : AppCompatActivity(), AuthFragmentInteraction, AuthOtpFragment.AuthCallback {
    @BindView(R.id.fl_circle_progress_container)
    internal lateinit var flLoadingContainer: FrameLayout

    @BindView(R.id.tv_auth_terms_conditions)
    internal lateinit var tvTermsAndConditions: TextView

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authViewModel: AuthViewModel by viewModels()
    private val navController: NavController by lazy { findNavController(R.id.nav_host_authentication) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        ButterKnife.bind(this)

        // Refresh and activate the remote config
        RemoteConfig.refreshAndActivate()

        val user = firebaseAuth.currentUser
        if (user != null && !PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            Log.v(TAG, "User already exists.")
        }

        tvTermsAndConditions.apply {
            text = Utils.fromHtml(getString(R.string.terms_and_condition_underlined))
            setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_app_terms_conditions).toUri())) }
        }

        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.authResult.observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Log.e(TAG, "calling successAuth")
                        successAuth(resource.data!!)
                        hideProgress()
                        toast("Welcome!")
                    }
                    Resource.Status.LOADING -> showProgress()
                    Resource.Status.ERROR_INVALID_REQUEST -> {
                        val error = resource.errorBody ?: return@Observer
                        when {
                            error.has("errors") -> {
                                val msg = error["errors"][0].asText()
                                toast(String.format(Locale.ENGLISH, "%s\nTry again.", msg))
                            }
                            resource.message != null -> toast(resource.message)
                        }
                        hideProgress()
                    }
                    else -> {
                        hideProgress()
                        toast(resource.message)
                    }
                }
            }
        })
    }

    private fun authenticateWithCredential(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        firebaseAuth.currentUser!!.getIdToken(false).addOnSuccessListener {
                            it.token?.also { token ->
                                authViewModel.authenticate(token)
                            } ?: Log.e(TAG, "Got null ID Token")
                        }
                    } else {
                        task.exception?.log(TAG, "Authentication failed")
                        toast(R.string.error_authentication)
                    }
                }
    }

    private fun askUserDetails() {
        navController.navigate(R.id.authDetailsFragment, null, navOptions { popUpTo(R.id.signInFragment) { inclusive = true } })
    }

    private fun showProgress() {
        flLoadingContainer.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        flLoadingContainer.visibility = View.GONE
    }

    override fun onGoogleAuth() {
        showProgress()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
        startActivityForResult(signInIntent, RC_AUTH_GOOGLE)
    }

    override fun onFacebookAuth(loginResult: LoginResult) {
        showProgress()
        val accessToken = loginResult.accessToken
        authViewModel.providerIdToken = accessToken.token
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        authenticateWithCredential(credential)
    }

    override fun onPhoneAuth(phoneNo: String) {
        val action = AuthOptionsFragmentDirections.actionVerifyOtp(phoneNo)
        navController.navigate(action)
    }

    private fun successAuth(data: AuthResultModel) {
        Handler().post {
            val authToken = data.token
            val account = Account(resources.getString(R.string.app_name), Constants.ACCOUNT_TYPE)
            val accountManager = AccountManager.get(this)
            val userData = Bundle()
            userData.putLong(Constants.ACCOUNT_UID, data.userId)
            accountManager.addAccountExplicitly(account, null, userData)
            accountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, authToken)

            startService(Intent(applicationContext, DeviceTokenService::class.java))
        }

        if (!data.isProfileReady) askUserDetails()
        else finishAuth()
    }

    override fun onSuccessVerification(credential: PhoneAuthCredential, idToken: String, phone: String) = authViewModel.authenticate(idToken)

    override fun onCancelVerification() {
    }

    override fun onFailedVerification(exception: Exception) {
        exception.log(TAG, "Authentication failed")
        toast(exception.message ?: getString(R.string.error_authentication_phone))
    }

    private fun finishAuth() {
        Utils.navigateBackToHome(this)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_AUTH_GOOGLE -> {
                GoogleSignIn.getSignedInAccountFromIntent(data).runCatching {
                    val account = getResult(ApiException::class.java)
                            ?: error("Null google account obtained")
                    authViewModel.providerIdToken = account.idToken
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    authenticateWithCredential(credential)
                }.onFailure { e ->
                    e.log(TAG, "GoogleAuth - Verification Failed: ")
                    hideProgress()
                    if (e is ApiException) toast(R.string.error_authentication_google)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private val TAG = AuthActivity::class.java.simpleName
        private const val RC_AUTH_GOOGLE = 1000
    }
}

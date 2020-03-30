package com.checkin.app.checkin.auth.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.auth.AuthResultModel
import com.checkin.app.checkin.auth.AuthViewModel
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException
import com.checkin.app.checkin.auth.fragments.AuthDetailsFragment
import com.checkin.app.checkin.auth.fragments.AuthOtpFragment
import com.checkin.app.checkin.auth.fragments.AuthOtpFragmentDirections
import com.checkin.app.checkin.auth.services.DeviceTokenService
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.activities.HomeActivity
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.toast
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.util.*

class AuthenticationActivity : AppCompatActivity(), AuthFragmentInteraction, AuthOtpFragment.AuthCallback {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        ButterKnife.bind(this)

        // Refresh and activate the remote config
        RemoteConfig.refreshAndActivate()

        val user = mAuth.currentUser

        if (user != null && !PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean(Constants.SP_SYNC_DEVICE_TOKEN, false)) {
            Log.v(TAG, "User already exists.")
        }
        authViewModel.authResult.observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        hideProgress()
                        toast("Welcome!")
                        successAuth(resource.data!!)
                    }
                    Resource.Status.LOADING -> showProgress()
                    Resource.Status.ERROR_INVALID_REQUEST -> if (authViewModel.isLoginAttempt) {
                        askUserDetails()
                    } else {
                        val error = resource.errorBody ?: return@Observer
                        when {
                            error.has("errors") -> {
                                val msg = error["errors"][0].asText()
                                toast(String.format(Locale.ENGLISH, "%s\nTry again.", msg))
                            }
                            error.has("username") -> {
                                authViewModel.showError(error)
                            }
                            resource.message != null -> toast(resource.message)
                        }
                    }
                    else -> {
                        hideProgress()
                        Utils.toast(this, resource.message)
                    }
                }
            }
        })
    }

    private fun authenticateWithCredential(credential: AuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        assert(mAuth.currentUser != null)
                        mAuth.currentUser!!.getIdToken(false).addOnSuccessListener { tokenResult: GetTokenResult ->
                            authViewModel.setFireBaseIdToken(tokenResult.token)
                            authViewModel.login()
                        }
                    } else if (task.exception != null) {
                        Utils.logErrors(TAG, task.exception, "Authentication failed")
                        Toast.makeText(applicationContext, R.string.error_authentication, Toast.LENGTH_SHORT).show()
                    }
                }
    }


    private fun askUserDetails() {
        val user = mAuth.currentUser

        val fragment: Fragment = AuthDetailsFragment.newInstance()
        if (user == null) {
            Log.e(TAG, "Logged-in user is NULL")
            return
        }
        val name = user.displayName
        if (name != null) {
            val action = AuthOtpFragmentDirections.actionOtpFragmentToAuthDetailsFragment(name)
            findNavController(R.id.nav_host_payment).navigate(action)
        }
        hideProgress()
    }


    private fun showProgress() {
        //TODO yet to add
    }

    private fun hideProgress() {
        //TODO yet to add
    }

    override fun onUserInfoProcess(firstName: String, lastName: String, username: String, gender: UserModel.GENDER) {
        authViewModel.register(firstName, lastName, gender, username)
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
        authViewModel.setProviderIdToken(accessToken.token)
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        authenticateWithCredential(credential)
    }

    override fun onPhoneAuth(phoneNo: String) {
        //not required ?
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
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    override fun onSuccessVerification(credential: PhoneAuthCredential, idToken: String) {
        authViewModel.setFireBaseIdToken(idToken)
        authViewModel.login()
        //ddddd dialog?.dismiss()
        //  hideDarkBack()
    }

    override fun onCancelVerification() {
        //  hideDarkBack()
    }

    override fun onFailedVerification(exception: Exception) {
        toast(exception.message ?: getString(R.string.error_authentication_phone))
        if (exception !is InvalidOTPException) {
            //dialog?.dismiss()
            //   hideDarkBack()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_AUTH_GOOGLE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    authViewModel.setProviderIdToken(account!!.idToken)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    authenticateWithCredential(credential)
                } catch (e: ApiException) {
                    Log.e(TAG, "GoogleAuth - Verification Failed: ", e)
                    hideProgress()
                    Toast.makeText(applicationContext, R.string.error_authentication_google, Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private val TAG = AuthenticationActivity::class.java.simpleName
        private const val RC_AUTH_GOOGLE = 1000
    }

}

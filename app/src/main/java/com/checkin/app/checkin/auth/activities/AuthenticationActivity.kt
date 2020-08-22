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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.AuthFragmentInteraction
import com.checkin.app.checkin.auth.AuthResultModel
import com.checkin.app.checkin.auth.AuthViewModel
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException
import com.checkin.app.checkin.auth.fragments.AuthOptionFragmentDirections
import com.checkin.app.checkin.auth.fragments.AuthOtpFragment
import com.checkin.app.checkin.auth.fragments.AuthOtpFragmentDirections
import com.checkin.app.checkin.auth.services.DeviceTokenService
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.user.models.UserModel
import com.checkin.app.checkin.user.viewmodels.UserViewModel
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.Utils.logErrors
import com.checkin.app.checkin.utility.pass
import com.checkin.app.checkin.utility.toast
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseError.ERROR_USER_NOT_FOUND
import com.google.firebase.auth.*
import java.util.*

class AuthenticationActivity : AppCompatActivity(), AuthFragmentInteraction, AuthOtpFragment.AuthCallback {
    @BindView(R.id.fl_circle_progress_container)
    internal lateinit var flLoadingContainer: FrameLayout

    @BindView(R.id.tv_auth_terms_conditions)
    internal lateinit var tvTermsAndConditions: TextView

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authViewModel: AuthViewModel by viewModels()
    private val navController: NavController by lazy { findNavController(R.id.nav_host_authentication) }
    private val userViewModel: UserViewModel by viewModels()

    private var goBack = true
    private lateinit var phoneNo: String
    private lateinit var phoneToken: String

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
                        authViewModel.isLoginAttempt = false
                        if (!::phoneNo.isInitialized) {
                            toast("Invalid Mobile Number")
                            pass
                        }
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
                        toast resource.message)
                    }
                }
            }
        })

        tvTermsAndConditions.apply {
            text = Utils.fromHtml(getString(R.string.terms_and_condition_underlined))
            setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_app_terms_conditions).toUri())) }
        }
    }

    private fun authenticateWithCredential(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        assert(firebaseAuth.currentUser != null)
                        firebaseAuth.currentUser!!.getIdToken(false).addOnSuccessListener { tokenResult: GetTokenResult ->
                            authViewModel.setFireBaseIdToken(tokenResult.token)
                            authViewModel.login()
                        }
                    } else if (task.exception != null) {
                        Utils.logErrors(TAG, task.exception, "Authentication failed")
                        toast( R.string.error_authentication)
                    }
                }
    }


    private fun askUserDetails() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            logErrors(TAG, FirebaseAuthInvalidUserException(ERROR_USER_NOT_FOUND.toString(), "Logged-in user is NULL"), "Logged-in user is NULL")
            return
        }
        val name = user.displayName
        goBack = false

        val fullNameSeparatedList = name?.split("\\s+".toRegex()) ?: listOf(defaultFirstName)
        //create an user
        onUserInfoProcess(firstName = fullNameSeparatedList[0], lastName = if (fullNameSeparatedList.size > 1) fullNameSeparatedList[1]
        else null, username = phoneNo, gender = defaultGender)

        authViewModel.authResult.observe(this, Observer {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        fetchUserDetails(name)
                        hideProgress()
                    }
                }
            }
        })

    }


    private fun showProgress() {
        flLoadingContainer.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        flLoadingContainer.visibility = View.GONE
    }

    override fun onUserInfoProcess(firstName: String, lastName: String?, username: String, gender: UserModel.GENDER) {
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
        val action = AuthOptionFragmentDirections.actionVerifyOtp(phoneNo)
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
            userViewModel.userData.observe(this, Observer {
                it?.let {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data?.firstName == defaultFirstName) {
                                fetchUserDetails(name = null)
                                //so if the firebase has  a name then that name would be the default name and the app will next time open with the
                                //name firebase gave otherwise if the username is Checkin(default name) then the case would be that the name would be checked
                                //if so that the name is checkin then the details screen would be fetched
                            } else {
                                Utils.navigateBackToHome(this)
                            }
                        }
                    }
                }
            })
            userViewModel.fetchUserData()
        }
    }


    override fun onSuccessVerification(credential: PhoneAuthCredential, idToken: String, phone: String) {
        authViewModel.setFireBaseIdToken(idToken)
        phoneNo = phone
        phoneToken = idToken
        authViewModel.login()
    }

    override fun onCancelVerification() {

    }

    override fun onFailedVerification(exception: Exception) {
        if (exception !is InvalidOTPException) {
            toast("Invalid OTP try Again")
        } else {
            toast(exception.message ?: getString(R.string.error_authentication_phone))
        }
    }

    override fun onBackPressed() {
        if (goBack) {
            super.onBackPressed()
        } else {
            toast("Authenticating the user, please wait!")
        }
    }

    private fun fetchUserDetails(name: String?) {
        val action = AuthOtpFragmentDirections.actionAddUserDetails(name, phoneNo, phoneToken)
        navController.navigate(action)
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
                    logErrors(TAG, e, "GoogleAuth - Verification Failed: ")
                    hideProgress()
                    toast(R.string.error_authentication_google)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private val TAG = AuthenticationActivity::class.java.simpleName
        const val defaultFirstName = "Checkin"
        val defaultGender = UserModel.GENDER.MALE
        private const val RC_AUTH_GOOGLE = 1000
    }

}

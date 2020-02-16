package com.checkin.app.checkin.Auth

import android.accounts.Account
import android.accounts.AccountManager
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.checkin.app.checkin.Auth.OtpVerificationDialog.AuthCallback
import com.checkin.app.checkin.Auth.SignupUserInfoFragment.Companion.KEY_NAME
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.home.activities.HomeActivity
import com.checkin.app.checkin.misc.EulaDialog
import com.checkin.app.checkin.user.models.UserModel.GENDER
import com.checkin.app.checkin.utility.Constants
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.toast
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.*

class AuthActivity : AppCompatActivity(), AuthFragmentInteraction, AuthCallback {
    @BindView(R.id.circle_progress)
    internal lateinit var vCircleProgress: ProgressBar
    @BindView(R.id.dark_back)
    internal lateinit var mDarkBack: View
    @BindView(R.id.tv_read_eula)
    internal lateinit var cbReadEula: TextView

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authViewModel: AuthViewModel by viewModels()
    private val eulaDialog: EulaDialog by lazy { EulaDialog(this) }

    private var goBack = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        ButterKnife.bind(this)

        // Refresh and activate the remote config
        RemoteConfig.refreshAndActivate()

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
                add(R.id.fragment_container, AuthOptionsFragment.newInstance())
            }
        }
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
        mDarkBack.setOnTouchListener { _, _ -> true }
    }

    private fun showProgress() {
        vCircleProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        vCircleProgress.visibility = View.GONE
    }

    private fun authenticateWithCredential(credential: AuthCredential?) {
        mAuth.signInWithCredential(credential!!)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        assert(mAuth.currentUser != null)
                        mAuth.currentUser!!.getIdToken(false).addOnSuccessListener { tokenResult: GetTokenResult ->
                            authViewModel.setFireBaseIdToken(tokenResult.token)
                            authViewModel.login()
                        }
                    } else {
                        Log.e(TAG, "Authentication failed", task.exception)
                        Toast.makeText(applicationContext, R.string.error_authentication, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun askUserDetails() {
        val user = mAuth.currentUser
        val fragment: Fragment = SignupUserInfoFragment.newInstance()
        if (user == null) {
            Log.e(TAG, "Logged-in user is NULL")
            return
        }
        val name = user.displayName
        if (name != null) {
            val bundle = Bundle()
            bundle.putString(KEY_NAME, name)
            fragment.arguments = bundle
        }
        goBack = false
        showDarkBack()
        hideProgress()
        replaceFragmentContainer(fragment)
    }

    private fun replaceFragmentContainer(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showDarkBack() {
        mDarkBack.animate()
                .alpha(0.67f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        mDarkBack.visibility = View.VISIBLE
                    }
                })
    }

    private fun hideDarkBack() {
        mDarkBack.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        mDarkBack.visibility = View.GONE
                    }
                })
    }

    @OnClick(R.id.tv_read_eula)
    fun readEula() {
        eulaDialog.show()
    }

    override fun onUserInfoProcess(firstName: String, lastName: String, userName: String, gender: GENDER) {
        authViewModel.register(firstName, lastName, gender, userName)
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
        val dialog = OtpVerificationDialog.Builder.with(this)
                .setAuthCallback(this)
                .build()
        dialog.verifyPhoneNumber(phoneNo)
        dialog.show()
        showDarkBack()
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

    override fun onSuccessVerification(dialog: DialogInterface?, credential: PhoneAuthCredential) {
        authenticateWithCredential(credential)
        dialog?.dismiss()
    }

    override fun onCancelVerification(dialog: DialogInterface?) {
        hideDarkBack()
    }

    override fun onFailedVerification(dialog: DialogInterface?, exception: FirebaseException) {
        dialog?.dismiss()
    }

    override fun onBackPressed() {
        if (goBack) {
            hideDarkBack()
            super.onBackPressed()
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
        private val TAG = AuthActivity::class.java.simpleName
        private const val RC_AUTH_GOOGLE = 1000
    }
}
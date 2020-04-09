package com.checkin.app.checkin.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.user.models.UserModel.GENDER
import com.fasterxml.jackson.databind.JsonNode

class AuthViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: AuthRepository = AuthRepository.getInstance(application)

    private val mErrors = MutableLiveData<JsonNode>()
    private val mAuthResult = createNetworkLiveData<AuthResultModel>()

    var firebaseIdToken: String? = null
        private set
    private var providerIdToken: String? = null

    var isLoginAttempt = false
        private set

    val authResult: MediatorLiveData<Resource<AuthResultModel>> = mAuthResult
    val errors: LiveData<JsonNode> = mErrors

    override fun updateResults() {}

    fun setFireBaseIdToken(idToken: String?) {
        firebaseIdToken = idToken
    }

    fun setProviderIdToken(idToken: String?) {
        providerIdToken = idToken
    }

    fun login() {
        if (firebaseIdToken == null) Log.e(TAG, "Firebase ID Token is NULL")
        val data = objectMapper.createObjectNode()
        data.put("id_token", firebaseIdToken)
        if (providerIdToken != null) data.put("provider_token", providerIdToken)
        isLoginAttempt = true
        mAuthResult.addSource(mRepository.login(data), mAuthResult::setValue)
    }

    fun register(firstName: String, lastName: String?, gender: GENDER, username: String) {
        if (firebaseIdToken == null) Log.e(TAG, "Firebase ID Token is NULL")
        val data = objectMapper.createObjectNode()
        data.put("username", username)
        data.put("id_token", firebaseIdToken)
        if (providerIdToken != null) data.put("provider_token", providerIdToken)
        data.put("first_name", firstName)
        data.put("gender", if (gender == GENDER.MALE) "m" else "f")
        data.put("last_name", lastName ?: "")
        isLoginAttempt = false
        mAuthResult.addSource(mRepository.register(data), mAuthResult::setValue)
    }

    fun showError(data: JsonNode) {
        mErrors.value = data
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
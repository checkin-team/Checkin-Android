package com.checkin.app.checkin.auth

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.fasterxml.jackson.databind.JsonNode

class AuthViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository: AuthRepository = AuthRepository.getInstance(application)

    private val mErrors = MutableLiveData<JsonNode>()
    private val mAuthResult = createNetworkLiveData<AuthResultModel>()

    var providerIdToken: String? = null

    val authResult: MediatorLiveData<Resource<AuthResultModel>> = mAuthResult

    override fun updateResults() {}

    fun authenticate(idToken: String) {
        val reqData = AuthRequestModel(idToken, providerIdToken ?: "")
        mAuthResult.addSource(mRepository.authenticate(reqData), mAuthResult::setValue)
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
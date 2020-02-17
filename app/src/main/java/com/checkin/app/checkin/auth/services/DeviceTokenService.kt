package com.checkin.app.checkin.auth.services

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.checkin.app.checkin.auth.AuthRepository
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.utility.Constants
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceTokenService : IntentService(TAG), OnSuccessListener<InstanceIdResult>, Callback<ObjectNode> {
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.takeIf { it.hasExtra(KEY_TOKEN) }) {
            null -> FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this)
            else -> saveToken(intent.getStringExtra(KEY_TOKEN))
        }
    }

    override fun onSuccess(instanceIdResult: InstanceIdResult) = saveToken(instanceIdResult.token)

    private fun saveToken(token: String) {
        AuthRepository.getInstance(application)
                .postDeviceToken(token)
                .enqueue(this)
    }

    private fun processResponse(response: ApiResponse<ObjectNode>) {
        if (response.isSuccessful) {
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .edit()
                    .putBoolean(Constants.SP_SYNC_DEVICE_TOKEN, true)
                    .apply()
        }
    }

    override fun onResponse(call: Call<ObjectNode>, response: Response<ObjectNode>) {
        processResponse(ApiResponse(response))
    }

    override fun onFailure(call: Call<ObjectNode>, t: Throwable) {
        processResponse(ApiResponse(t))
    }

    companion object {
        const val KEY_TOKEN = "device_token"
        private val TAG = DeviceTokenService::class.java.simpleName
    }

    init {
        Log.e(TAG, "Service started!!")
    }
}
package com.checkin.app.checkin.location

import android.app.ActivityManager
import android.app.IntentService
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.location.Location
import android.os.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.checkin.app.checkin.data.network.ApiResponse
import com.checkin.app.checkin.data.notifications.Constants
import com.checkin.app.checkin.data.notifications.MessageUtils
import com.checkin.app.checkin.misc.models.LocationModel
import com.checkin.app.checkin.user.UserRepository
import com.checkin.app.checkin.user.models.LocationTag
import com.checkin.app.checkin.user.models.UserLocationModel
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * An [IntentService] subclass for handling asynchronous task of users location
 */
class UserCurrentLocationService : Service(), Callback<UserLocationModel> {
    private var isConfigChanged = false

    private val binder = LocationServiceBinder()
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                result?.let { onNewLocation(it.lastLocation) }
            }
        }
    }
    private var shouldTrackLocation = false
    private val repository by lazy { UserRepository.getInstance(application) }
    private val locationRequest: LocationRequest by lazy { createLocationRequest() }
    private val addressReceiver = AddressReceiver()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = intent?.let {
        Log.i(TAG, "Location Track service started")

        startInForeground()
        when {
            it.action == Constants.SERVICE_ACTION_FOREGROUND_STOP -> {
                if (shouldTrackLocation) removeLocationUpdates()
                stopForeground(true)
                stopSelf()
            }
            it.getBooleanExtra(KEY_TRACK_LOCATION, false) -> {
                shouldTrackLocation = true
                requestLocationUpdates()
            }
            else -> { // Just get the current location
                shouldTrackLocation = false
                getLastLocation()
            }
        }

        START_NOT_STICKY
    } ?: START_NOT_STICKY

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isConfigChanged = true
    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        isConfigChanged = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        isConfigChanged = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")
        if (!isConfigChanged) startInForeground()
        return true
    }

    private fun startInForeground() {
        Log.i(TAG, "Starting foreground service")
        startForeground(NOTIFICATION_ID, getNotification())
    }

    private fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates.", unlikely)
        }
    }

    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")

        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates.", unlikely)
        }
    }

    private fun getNotification(): Notification = MessageUtils.createLocationTrackNotification(this).build()

    private fun getLastLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) task.result?.let { onNewLocation(it) }
                else Log.w(TAG, "Failed to get last location.")
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.", unlikely)
        }
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        val serviceIntent = Intent(this, FetchAddressIntentService::class.java).apply {
            putExtra(AppUtils.LocationConstants.RECEIVER, addressReceiver)
            putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, location)
        }
        startService(serviceIntent)
    }

    internal fun pushLocationData(location: Location, address: Address) {
        val data = UserLocationModel(LocationModel(address), LocationTag.CURRENT)
        repository.postUserCurrentLocation(data)
                .enqueue(this)
    }

    override fun onFailure(call: Call<UserLocationModel>, t: Throwable) {
        processResponse(ApiResponse(t))
    }

    override fun onResponse(call: Call<UserLocationModel>, response: Response<UserLocationModel>) {
        processResponse(ApiResponse(response))
    }

    private fun processResponse(response: ApiResponse<UserLocationModel>) {
        if (response.isSuccessful) {
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(ACTION_BROADCAST_LOCATION).apply {
                        putExtra(KEY_TRACK_LOCATION, shouldTrackLocation)
                        putExtra(KEY_ACTION_LOCATION_COORDINATES, response.data?.location?.coordinates)
                    })
            if (!shouldTrackLocation) stopSelf()
        } else {
            Log.e(TAG, response.errorMessage, response.errorThrowable)
            if (!shouldTrackLocation) getLastLocation()
        }
    }

    private fun isRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return try {
            manager.getRunningServices(Int.MAX_VALUE).find { it.service.className == TAG }?.foreground
                    ?: false
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "running in foreground?", unlikely)
            false
        }
    }

    companion object {
        private val TAG = UserCurrentLocationService::class.simpleName

        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private const val NOTIFICATION_ID = 709
        private const val KEY_TRACK_LOCATION = "com.checkin.location.track"

        const val ACTION_BROADCAST_LOCATION = "com.checkin.location.broadcast"
        const val KEY_ACTION_LOCATION_COORDINATES = "com.checkin.location.coordinates"

        private fun createLocationRequest(): LocationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    inner class LocationServiceBinder : Binder()

    inner class AddressReceiver : ResultReceiver(Handler()) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT && resultData != null) {
                val location = resultData.getParcelable<Location>(AppUtils.LocationConstants.LOCATION_DATA_LOCATION_BUNDLE)
                val address = resultData.getParcelable<Address>(AppUtils.LocationConstants.LOCATION_DATA_ADDRESS_BUNDLE)
                if (location != null && address != null) pushLocationData(location, address)
                else if (!shouldTrackLocation) getLastLocation()
            }
        }
    }
}

package com.checkin.app.checkin.User


import android.content.Intent
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import android.app.*
import android.content.res.Configuration
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat


/**
 * An [IntentService] subclass for handling asynchronous task of users location
 */


class UserCurrentLocationService : Service() {


    companion object {
        val PACKAGE_NAME = "com.silverpants.hello.UserLocarionService"
        val TAG = UserCurrentLocationService.javaClass.simpleName
        val CHANNEL_ID = "channel_01"
        val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000
        val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
                UPDATE_INTERVAL_IN_MILLISECONDS / 2
        val EXTRA_LOCATION = "$PACKAGE_NAME.location"

        val NOTIFICATION_ID = 12345678


    }

    var changeConfig = false

    lateinit var notificationManager: NotificationManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    lateinit var location: Location
    lateinit var handler: Handler
    val binder: Binder = LocalBinder()


    override fun onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult!!.lastLocation)
            }
        }

        locationRequest = createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()

        handler = Handler(handlerThread.looper)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //android O backgroundLimitsHandling

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App name"

            val mChannel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)


            notificationManager.createNotificationChannel(mChannel)
        }



        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")

        val startedFromNotification = intent!!.getBooleanExtra(
                EXTRA_STARTED_FROM_NOTIFICATION,
                false
        )

        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }


        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeConfig = true
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "in onBind()");
        stopForeground(true)
        changeConfig = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        changeConfig = false;
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")
        if (!changeConfig) {//  && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service")

            startForeground(NOTIFICATION_ID, getNotification())
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")

//        Utils.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, UserCurrentLocationService::class.java))


        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        } catch (unlikely: SecurityException) {
//            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely");
        }
    }


    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")

        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//            Utils.setRequestingLocationUpdates(this, false)
            stopSelf()

        } catch (unlikely: SecurityException) {
//            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    fun getNotification(): Notification {
        val intent = Intent(this, UserCurrentLocationService.javaClass)
        val text = "hbjjv"//Utils.getLocationText(location)
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        val servicePendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val activityPendingIntent =
//                PendingIntent.getActivity(this, 0, Intent(this, MainActivity.javaClass), 0)

        val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
//                        .addAction(
//                                R.mipmap.ic_launcher, getString(R.string.app_name),
//                                activityPendingIntent
//                        )
//                        .addAction(
//                                R.drawable.ic_cancel, getString(R.string.remove_location_updates),
//                                servicePendingIntent
//                        )
                        .setContentText(text)
//                        .setContentTitle(Utils.getLocationTitle(this))
                        .setOngoing(true)
//                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker(text)
                        .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }

        return builder.build()
    }

    private fun getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            location = task.result!!

                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }

    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: " + location);
        this.location = location
//        val intent = Intent(ACTION_BROADCAST)
//        intent.putExtra(EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        if (serviceIsRunningInForeground(this)) {
            notificationManager.notify(NOTIFICATION_ID, getNotification())
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): UserCurrentLocationService = this@UserCurrentLocationService
    }

    fun createLocationRequest(): LocationRequest = LocationRequest.create().apply {
        UPDATE_INTERVAL_IN_MILLISECONDS
        FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        PRIORITY_HIGH_ACCURACY
    }

    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager: ActivityManager = context.getSystemService(
                Context.ACTIVITY_SERVICE
        ) as ActivityManager


        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (UserCurrentLocationService::class.simpleName == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

}

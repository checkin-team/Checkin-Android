package com.checkin.app.checkin.data.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.ProgressRequestBody.UploadCallbacks
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.data.notifications.Constants.CHANNEL
import com.checkin.app.checkin.data.notifications.Constants.CHANNEL_GROUP
import com.checkin.app.checkin.data.notifications.Constants.FORMAT_SP_KEY_NOTIFICATION_CHANNEL
import com.checkin.app.checkin.data.notifications.Constants.KEY_DATA
import com.checkin.app.checkin.data.notifications.Constants.SP_TABLE_NOTIFICATION
import com.checkin.app.checkin.data.notifications.Constants.getAlertOrdersSoundUri
import com.checkin.app.checkin.data.notifications.Constants.getNotificationTag
import com.checkin.app.checkin.data.notifications.Constants.notificationID
import com.checkin.app.checkin.data.notifications.MessageObjectModel.MESSAGE_OBJECT_TYPE
import java.util.*

object MessageUtils {
    private val TAG = MessageUtils::class.java.simpleName
    private val notifTagToIds: MutableMap<String, MutableList<Int>> = HashMap()
    private val sHandler = Handler(Looper.myLooper())

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannels(group: CHANNEL_GROUP, importance: Int, vararg channelTypes: CHANNEL) = channelTypes.map {
        NotificationChannel(it.id, it.title, importance).apply { this.group = group.id }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannelGroups(notificationManager: NotificationManager, vararg channelGroups: CHANNEL_GROUP) {
        val groups: MutableList<NotificationChannelGroup> = ArrayList(channelGroups.size)
        for (channelGroup in channelGroups) {
            groups.add(NotificationChannelGroup(channelGroup.id, channelGroup.title))
        }
        notificationManager.createNotificationChannelGroups(groups)
    }

    @JvmStatic
    fun createDefaultChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.DEFAULT_USER, CHANNEL_GROUP.RESTAURANT_CUSTOMER, CHANNEL_GROUP.MISC)
            val channels = createChannels(CHANNEL_GROUP.DEFAULT_USER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.DEFAULT) +
                    createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.ACTIVE_SESSION) +
                    createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.ACTIVE_SESSION_PERSISTENT) +
                    createChannels(CHANNEL_GROUP.MISC, NotificationManager.IMPORTANCE_LOW, CHANNEL.MEDIA_UPLOAD) +
                    createChannels(CHANNEL_GROUP.MISC, NotificationManager.IMPORTANCE_LOW, CHANNEL.LOCATION_TRACK)
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun createManagerChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER)
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.MANAGER)
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun createWaiterChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER)
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.WAITER)
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun createAdminChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER)
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.MEMBER, CHANNEL.ADMIN)
            notificationManager.createNotificationChannels(channels)
        }
    }

    fun createActiveCustomerChannels(notificationManager: NotificationManager?) {
        if (notificationManager == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_CUSTOMER)
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_MAX, CHANNEL.ACTIVE_SESSION_PERSISTENT)
            notificationManager.createNotificationChannels(channels)
            channels[0].lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
    }

    fun createScheduledSessionChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_CUSTOMER)
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_MAX, CHANNEL.SCHEDULED_SESSION)
            notificationManager.createNotificationChannels(channels)
            channels[0].lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
    }

    fun createOrderChannels(context: Context, notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER)
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            val channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.ORDERS)
            channels.map {
                it.setSound(getAlertOrdersSoundUri(context), audioAttributes)
                it.enableVibration(true)
                it.enableLights(true)
                it.setBypassDnd(true)
            }
            notificationManager.createNotificationChannels(channels)
        }
    }

    @JvmStatic
    fun registerLocalReceiver(context: Context, receiver: BroadcastReceiver?, vararg types: MESSAGE_TYPE) {
        val intentFilter = IntentFilter()
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY)
        for (type in types) intentFilter.addAction(type.actionTag)
        LocalBroadcastManager.getInstance(context.applicationContext)
                .registerReceiver(receiver!!, intentFilter)
    }

    @JvmStatic
    fun registerLocalReceiver(context: Context, receiver: BroadcastReceiver?, targetPk: Long, vararg types: MESSAGE_TYPE) {
        val intentFilter = IntentFilter()
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY)
        intentFilter.addDataScheme(Constants.FILTER_DATA_SCHEME)
        intentFilter.addDataPath(String.format(Locale.ENGLISH, Constants.FILTER_DATA_TARGET_PATH, targetPk), 0)
        for (type in types) intentFilter.addAction(type.actionTag)
        LocalBroadcastManager.getInstance(context.applicationContext)
                .registerReceiver(receiver!!, intentFilter)
    }

    @JvmStatic
    fun unregisterLocalReceiver(context: Context, receiver: BroadcastReceiver?) {
        LocalBroadcastManager.getInstance(context.applicationContext)
                .unregisterReceiver(receiver!!)
    }

    @JvmStatic
    fun sendLocalBroadcast(context: Context, data: MessageModel): Boolean {
        val intent = Intent(data.type.actionTag)
        intent.addCategory(Constants.FCM_INTENT_CATEGORY)
        intent.putExtra(Constants.KEY_DATA, data)
        return LocalBroadcastManager.getInstance(context.applicationContext).sendBroadcast(intent)
    }

    @JvmStatic
    fun createUploadNotification(context: Context): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, CHANNEL.MEDIA_UPLOAD.id)
        builder.setContentTitle("Media upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(100, 0, false)
        return builder
    }

    fun createLocationTrackNotification(context: Context) = NotificationCompat.Builder(context, CHANNEL.LOCATION_TRACK.id).apply {
        setContentTitle("Checkin")
        setContentText("Detecting Location")
        setOngoing(true)
        setTicker("Now")
    }

    @JvmStatic
    fun parseMessage(intent: Intent): MessageModel? = try {
        intent.getSerializableExtra(KEY_DATA) as MessageModel
    } catch (e: ClassCastException) {
        Log.e(TAG, "Invalid message object received.")
        e.printStackTrace()
        null
    }

    @JvmStatic
    fun createRequiredChannel(channel: CHANNEL?, context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        when (channel) {
            CHANNEL.ADMIN, CHANNEL.MEMBER -> createAdminChannels(notificationManager)
            CHANNEL.WAITER -> createWaiterChannels(notificationManager)
            CHANNEL.MANAGER -> createManagerChannels(notificationManager)
            CHANNEL.ORDERS -> createOrderChannels(context, notificationManager)
            CHANNEL.ACTIVE_SESSION_PERSISTENT -> createActiveCustomerChannels(notificationManager)
            CHANNEL.SCHEDULED_SESSION -> createScheduledSessionChannel(notificationManager)
            else -> createDefaultChannels(notificationManager)
        }
    }

    @JvmStatic
    fun setEnableNotification(context: Context, status: Boolean, vararg channels: CHANNEL?) {
        val sharedPreferences = context.getSharedPreferences(SP_TABLE_NOTIFICATION, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        for (channel in channels) {
            editor.putBoolean(String.format(Locale.ENGLISH, FORMAT_SP_KEY_NOTIFICATION_CHANNEL, channel), status)
        }
        editor.apply()
    }

    @JvmStatic
    fun isNotificationEnabled(context: Context, channel: CHANNEL?): Boolean {
        val sharedPreferences = context.getSharedPreferences(SP_TABLE_NOTIFICATION, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(String.format(Locale.ENGLISH, FORMAT_SP_KEY_NOTIFICATION_CHANNEL, channel), true)
    }

    @JvmStatic
    fun dismissNotification(context: Context, objectType: MESSAGE_OBJECT_TYPE?, objectPk: Long) {
        val notifTag = getNotificationTag(objectType!!, objectPk)
        sHandler.post {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return@post
            notifTagToIds[notifTag]?.forEach { notificationManager.cancel(notifTag, it) }
            notifTagToIds.remove(notifTag)
        }
    }

    @JvmStatic
    fun saveNotificationId(notifTag: String, notificationId: Int) {
        sHandler.post {
            val notifIdList = Utils.getOrDefault(notifTagToIds, notifTag, ArrayList())
            notifIdList.add(notificationId)
            notifTagToIds[notifTag] = notifIdList
        }
    }

    open class NotificationUpdate(context: Context, val builder: NotificationCompat.Builder) : UploadCallbacks {
        private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        private val notificationId: Int = notificationID

        override fun onProgressUpdate(percentage: Int) {
            Log.d("Update Status", percentage.toString() + "")
            builder.setProgress(100, percentage, false)
            notificationManager.notify(notificationId, builder.build())
        }

        override fun onSuccess() {
            builder.setContentText("Upload completed.")
                    .setProgress(0, 0, false)
                    .setAutoCancel(true)
            notificationManager.notify(notificationId, builder.build())
        }

        override fun onFailure() {
            builder.setContentText("Upload error.")
                    .setProgress(0, 0, false)
                    .setAutoCancel(true)
            notificationManager.notify(notificationId, builder.build())
        }

        init {
            notificationManager.notify(notificationId, builder.build())
        }
    }
}
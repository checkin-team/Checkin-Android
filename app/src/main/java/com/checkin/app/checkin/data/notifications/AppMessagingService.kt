package com.checkin.app.checkin.data.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.checkin.app.checkin.R
import com.checkin.app.checkin.auth.services.DeviceTokenService
import com.checkin.app.checkin.data.Converters.objectMapper
import com.checkin.app.checkin.data.notifications.Constants.notificationID
import com.checkin.app.checkin.data.notifications.MessageUtils.createDefaultChannels
import com.checkin.app.checkin.data.notifications.MessageUtils.isNotificationEnabled
import com.checkin.app.checkin.data.notifications.MessageUtils.saveNotificationId
import com.checkin.app.checkin.data.notifications.MessageUtils.sendLocalBroadcast
import com.checkin.app.checkin.utility.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class AppMessagingService : FirebaseMessagingService() {
    private val mNotificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createDefaultChannels(mNotificationManager)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val params = remoteMessage.data
        val clz = if (params.containsKey("type")) MessageModel::class.java else PromotionalMessageModel::class.java
        val data = objectMapper.runCatching { writeValueAsString(params) }
                .onFailure {
                    Log.e(TAG, "Couldn't parse FCM remote data.", it)
                    return
                }.getOrNull()?.let {
                    objectMapper.runCatching { readValue(it, clz) }
                            .onFailure { Log.e(TAG, "Couldn't parse FCM remote data as ${clz.simpleName}.", it) }
                            .getOrNull()
                }
        Log.e(TAG, data.toString())
        when (data) {
            is MessageModel -> {
                var shouldShowNotification: Boolean
                if (data.shouldTryUpdateUi()) {
                    val result = sendLocalBroadcast(this, data)
                    shouldShowNotification = data.shouldShowNotification()
                    if (data.isOnlyUiUpdate) shouldShowNotification = !result && shouldShowNotification
                } else shouldShowNotification = true
                if (shouldShowNotification) showNotification(data)
            }
            is PromotionalMessageModel -> showPromoNotification(data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showGroupedNotifications(data: MessageModel) {
        val notifGroup = data.groupKey
        var notifCount = 1
        val style = NotificationCompat.InboxStyle()
        for (statusBarNotification in mNotificationManager.activeNotifications) {
            if (statusBarNotification.notification.group == null) continue
            if (statusBarNotification.notification.group == notifGroup && statusBarNotification.tag != null) {
                style.addLine(statusBarNotification.notification.extras.getString(Notification.EXTRA_TEXT, ""))
                notifCount++
            }
        }
        if (notifCount == 1) return
        style.setBigContentTitle(data.groupTitle)
                .setSummaryText(String.format(Locale.getDefault(), "%d events", notifCount))
                .addLine(data.description)
        val groupTitle = String.format(Locale.getDefault(), "%s - %d events", data.groupTitle, notifCount)
        val intent = data.getGroupIntent(this)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val summaryNotif = NotificationCompat.Builder(this, data.channel.id)
                .setContentTitle(this.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentText(groupTitle)
                .setGroup(notifGroup)
                .setGroupSummary(true)
                .setStyle(style)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val notifTag = data.notificationTag
        val notifId = data.groupSummaryID
        mNotificationManager.notify(notifTag, notifId, summaryNotif)
        saveNotificationId(notifTag, notifId)
    }

    @SuppressLint("NewApi")
    private fun showNotification(data: MessageModel) {
        if (isNotificationEnabled(applicationContext, data.channel)) {
            val notificationId = notificationID
            val notification = data.showNotification(this)
            val notifTag = data.notificationTag
            mNotificationManager.notify(notifTag, notificationId, notification)
            saveNotificationId(notifTag, notificationId)
            if (Utils.isMarshMallowOrLater && data.isGroupedNotification) showGroupedNotifications(data)
        }
    }

    private fun showPromoNotification(data: PromotionalMessageModel) {
        val notificationId = notificationID
        val notification = data.showNotification(this)
        val notifTag = data.notificationTag
        mNotificationManager.notify(notifTag, notificationId, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Intent(applicationContext, DeviceTokenService::class.java).apply {
            putExtra(DeviceTokenService.KEY_TOKEN, token)
        }.runCatching { startService(this) }
    }

    companion object {
        private val TAG = AppMessagingService::class.java.simpleName
    }
}
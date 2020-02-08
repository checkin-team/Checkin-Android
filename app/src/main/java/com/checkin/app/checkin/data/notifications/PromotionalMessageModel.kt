package com.checkin.app.checkin.data.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.checkin.app.checkin.R
import com.checkin.app.checkin.utility.Utils

data class PromotionalMessageModel(
        val title: String,
        val description: String?,
        val targetLink: String
) {
    val notificationTag: String = Constants.getPromoNotificationTag(targetLink)

    val formatDescription: CharSequence? = description?.let { Utils.fromHtml(it) }

    private fun getNotificationIntent(context: Context) = Intent(Intent.ACTION_VIEW, targetLink.toUri())

    private fun getNotificationBuilder(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        var summary = formatDescription
        var isBigText = false
        if (summary?.length ?: 0 > 30) {
            isBigText = true
            summary = summary!!.substring(0, 30) + "..."
        }
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL.PROMOTIONAL.id)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true)
        if (summary != null) {
            builder.setContentText(summary)
            if (isBigText) builder.setStyle(NotificationCompat.BigTextStyle().bigText(formatDescription))
        }
        addNotificationExtra(context, builder, pendingIntent)
        return builder
    }

    private fun addNotificationExtra(context: Context, builder: NotificationCompat.Builder, pendingIntent: PendingIntent) {}

    fun showNotification(context: Context): Notification? {
        val intent = getNotificationIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        return getNotificationBuilder(context, pendingIntent)
                .setContentIntent(pendingIntent)
                .build()
    }
}
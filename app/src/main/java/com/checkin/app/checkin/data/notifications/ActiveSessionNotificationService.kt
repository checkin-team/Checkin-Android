package com.checkin.app.checkin.data.notifications

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.request.target.NotificationTarget
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.GlideApp
import com.checkin.app.checkin.data.notifications.MessageUtils.createRequiredChannel
import com.checkin.app.checkin.menu.activities.ActiveSessionMenuActivity.Companion.openMenu
import com.checkin.app.checkin.misc.models.BriefModel
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity

class ActiveSessionNotificationService : Service() {
    private var mNotification: Notification? = null
    private var mRestaurantPk: Long = 0
    private var mSessionPk: Long = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        if (Constants.SERVICE_ACTION_FOREGROUND_START == intent.action) {
            if (mNotification == null) showNotification(intent)
        } else if (Constants.SERVICE_ACTION_FOREGROUND_STOP == intent.action) {
            stopForeground(true)
            stopSelf()
        } else if (ACTION_OPEN_MENU == intent.action) {
            openMenu()
            val hideNotificationDrawerIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            this.sendBroadcast(hideNotificationDrawerIntent)
        }
        return START_STICKY
    }

    private fun showNotification(intent: Intent) {
        val restaurantDetail = intent.getSerializableExtra(ACTIVE_RESTAURANT_DETAIL) as BriefModel
        mSessionPk = intent.getLongExtra(ACTIVE_SESSION_PK, 0)
        mRestaurantPk = restaurantDetail.pk
        if (mSessionPk == 0L) return
        val restaurant_name = "At " + restaurantDetail.displayName
        val restaurant_logo = restaurantDetail.displayPic
        val bigViews = RemoteViews(packageName, R.layout.notification_persistent_active_session)
        bigViews.setTextViewText(R.id.tv_as_noti_restaurant_name, restaurant_name)
        val activeSessionIntent = Intent(this, ActiveSessionActivity::class.java)
        activeSessionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, activeSessionIntent, 0)
        val menuIntent = Intent(this, ActiveSessionNotificationService::class.java)
        menuIntent.action = ACTION_OPEN_MENU
        val pMenuIntent = PendingIntent.getService(this, 0, menuIntent, 0)
        bigViews.setOnClickPendingIntent(R.id.ll_as_noti_live_container, pendingIntent)
        bigViews.setOnClickPendingIntent(R.id.ll_menu, pMenuIntent)
        mNotification = NotificationCompat.Builder(this, Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT.id)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Live")
                .setContentText(restaurant_name)
                .setStyle(NotificationCompat.BigTextStyle())
                .setCustomContentView(bigViews)
                .setCustomBigContentView(bigViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.id.ll_menu, "Menu", pMenuIntent)
                .build()
        val notificationTarget = NotificationTarget(
                this,
                R.id.im_as_noti_restaurant_logo,
                bigViews,
                mNotification,
                ID_FOREGROUND_SERVICE)
        GlideApp
                .with(applicationContext)
                .asBitmap()
                .load(restaurant_logo)
                .into(notificationTarget)
        createRequiredChannel(Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT, this)
        startForeground(ID_FOREGROUND_SERVICE, mNotification)
    }

    private fun openMenu() {
        val menuIntent = openMenu(this, mRestaurantPk)
        menuIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(menuIntent)
    }

    companion object {
        const val ACTIVE_RESTAURANT_DETAIL = "active.restaurant.detail"
        const val ACTIVE_SESSION_PK = "active.session.pk"
        const val ACTION_OPEN_MENU = "active.menu.open"
        private const val ID_FOREGROUND_SERVICE = 501
        @JvmStatic
        fun clearNotification(context: Context) {
            val serviceIntent = Intent(context, ActiveSessionNotificationService::class.java)
                    .setAction(Constants.SERVICE_ACTION_FOREGROUND_STOP)
            context.startService(serviceIntent)
        }
    }
}
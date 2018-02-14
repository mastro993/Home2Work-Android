package it.gruppoinfor.home2work.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.SplashActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User

class MessagingService : FirebaseMessagingService() {

    private val TYPE = "TYPE"
    private val MATCHES_CHANNELL = "MATCHES_CHANNEL"
    private lateinit var broadcaster: LocalBroadcastManager

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data[TYPE]
            if (!type.isNullOrEmpty()) {
                val intent = Intent(type)
                broadcaster.sendBroadcast(intent)
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification!!)
        }

    }

    private fun sendNotification(notification: RemoteMessage.Notification) {

        val user: User? = HomeToWorkClient.getUser()

        val resultIntent = if (user != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SplashActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(this, "BOOKING_NOTIFICATION_CHANNEL")
                .setSmallIcon(R.drawable.home2work_icon)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val mNotificationId = 32425

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(mNotifyMgr)
        }

        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notifyMgr: NotificationManager) {
        val followersChannel = NotificationChannel(
                MATCHES_CHANNELL,
                "Matches Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT)

        followersChannel.lightColor = Color.GREEN
        followersChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 500)

        notifyMgr.createNotificationChannel(followersChannel)
    }

}

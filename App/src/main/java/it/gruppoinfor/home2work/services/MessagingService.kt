package it.gruppoinfor.home2work.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.SplashActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient

class MessagingService : FirebaseMessagingService() {
    private var broadcaster: LocalBroadcastManager? = null

    private var notification: RemoteMessage.Notification? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // Check if message contains a data payload.
        if (remoteMessage!!.data.size > 0) {
            processData(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            notification = remoteMessage.notification
            sendNotification()
        }

    }

    private fun processData(data: Map<String, String>) {
        if (data[TYPE] != null) {
            val intent = Intent(data[TYPE])
            broadcaster!!.sendBroadcast(intent)
        }
    }

    private fun sendNotification() {

        val pendingIntent: PendingIntent
        if (HomeToWorkClient.getUser() != null) {
            val resultIntent = Intent(this, MainActivity::class.java)
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            val resultIntent = Intent(this, SplashActivity::class.java)
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val mBuilder = NotificationCompat.Builder(this, "BOOKING_NOTIFICATION_CHANNEL")
                .setSmallIcon(R.drawable.home2work_icon)
                .setContentTitle(notification!!.title)
                .setContentText(notification!!.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)


        val mNotificationId = 32425

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_channel_01", "Canale prenotazioni", NotificationManager.IMPORTANCE_DEFAULT)
            mNotifyMgr?.createNotificationChannel(channel)
        }

        mNotifyMgr?.notify(mNotificationId, mBuilder.build())
    }

    companion object {

        val SHARE_JOIN_REQUEST = "SHARE_JOIN_REQUEST"
        val SHARE_COMPLETE_REQUEST = "SHARE_COMPLETE_REQUEST"
        val SHARE_DETACH_REQUEST = "SHARE_COMPLETE_REQUEST"
        private val TYPE = "TYPE"
    }
}

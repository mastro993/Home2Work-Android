package it.gruppoinfor.home2work.services


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.InboxActivity
import it.gruppoinfor.home2work.utils.Const


class MessagingService : FirebaseMessagingService() {


    private lateinit var broadcaster: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()

        broadcaster = LocalBroadcastManager.getInstance(this)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            sendIntent(remoteMessage)
        }

        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage)
        }

    }

    private fun sendIntent(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data[Const.TYPE]
        when (type) {
            Const.NEW_MESSAGE_RECEIVED -> {
                val intent = Intent(type)
                intent.putExtra(Const.CHAT_ID, remoteMessage.data[Const.CHAT_ID]?.toLong())
                intent.putExtra(Const.TEXT, remoteMessage.data[Const.TEXT])
                broadcaster.sendBroadcast(intent)
            }
            else -> {
                val intent = Intent(type)
                broadcaster.sendBroadcast(intent)
            }
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isEmpty()) return
        val type = remoteMessage.data[Const.TYPE]
        if (type.isNullOrEmpty()) return

        when (type) {
            Const.NEW_MESSAGE_RECEIVED -> {

                val resultIntent = Intent(this, InboxActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel() else ""
                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                val notification = notificationBuilder.setAutoCancel(true)
                        .setSmallIcon(R.drawable.home2work_icon)
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.notification?.title)
                        .setContentText(remoteMessage.notification?.body)
                        .setContentIntent(pendingIntent)
                        .build()


                mNotifyMgr.notify(Const.MESSAGING_NOTIFICATION_ID, notification)

            }
            Const.NEW_MATCHES -> {
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {

        val channelId = "hometowork_messages"
        val channelName = "Messaggi HomeToWork"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId

    }

}

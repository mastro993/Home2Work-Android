package it.gruppoinfor.home2work.services


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
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

        val notification = remoteMessage.notification
        when (type) {
            Const.NEW_MESSAGE_RECEIVED -> {

                val resultIntent = Intent(this, InboxActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val mBuilder = NotificationCompat.Builder(this, "MESSAGING_CHANNEL")
                        .setSmallIcon(R.drawable.home2work_icon)
                        .setContentTitle(notification?.title)
                        .setContentText(notification?.body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_LOW)

                val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(mNotifyMgr, "Messaging channel", Const.MESSAGING_CHANNELL, NotificationManager.IMPORTANCE_MIN)
                }

                mNotifyMgr.notify(Const.MESSAGING_NOTIFICATION_ID, mBuilder.build())

            }
            Const.NEW_MATCHES -> {
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notifyMgr: NotificationManager, id: String, name: String, importance: Int) {

        val channel = NotificationChannel(id, name, importance)
        channel.lightColor = Color.GREEN
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 500)
        notifyMgr.createNotificationChannel(channel)

    }

}

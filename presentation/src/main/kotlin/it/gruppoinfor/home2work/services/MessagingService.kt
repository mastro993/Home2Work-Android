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
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.events.ActiveShareEvent
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.chat.ChatActivity
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject


class MessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var storeUserFCMToken: StoreUserFCMToken
    @Inject
    lateinit var localUserData: LocalUserData

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)
    }

    override fun onNewToken(token: String) {
        Timber.d("MessagingService: %1s", "onNewToken")

        val savedToken = localUserData.firebaseToken
        if (token != savedToken) {
            storeUserFCMToken.store(token)
                    .subscribe({
                        Timber.i("Token Firebase aggiornato")
                        localUserData.firebaseToken = token
                    }, {
                        Timber.w(it, "Impossibile aggiornare il token Firebase")
                    })
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("MessagingService: %1s", "onMessageReceived")

        if (remoteMessage.data.isNotEmpty()) {
            sendIntent(remoteMessage)
        }

        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage)
        }

    }

    private fun sendIntent(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data[TYPE]
        when (type) {
            NEW_MESSAGE_RECEIVED -> {
                val chatId = remoteMessage.data[CHAT_ID]?.toLong()
                val messageId = remoteMessage.data[MESSAGE_ID]?.toLong()
                EventBus.getDefault().post(NewMessageEvent(chatId, messageId))
            }
            SHARE_JOIN, SHARE_COMPLETED, SHARE_LEAVED -> {
                EventBus.getDefault().post(ActiveShareEvent())
            }
            SHARE_BAN, SHARE_CANCELED -> {
                EventBus.getDefault().post(ActiveShareEvent(finished = true))
            }
            else -> {
                //val intent = Intent(type)
                //broadcaster.sendBroadcast(intent)
            }
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isEmpty()) return
        val type = remoteMessage.data[TYPE]
        if (type.isNullOrEmpty()) return

        when (type) {
            NEW_MESSAGE_RECEIVED -> {

                val resultIntent = Intent(this, ChatActivity::class.java)
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


                mNotifyMgr.notify(MESSAGING_NOTIFICATION_ID, notification)

            }
            SHARE_CANCELED, SHARE_LEAVED, SHARE_BAN -> {


                val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel() else ""
                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                val notification = notificationBuilder.setAutoCancel(true)
                        .setSmallIcon(R.drawable.home2work_icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.notification?.title)
                        .setContentText(remoteMessage.notification?.body)
                        .setVibrate(longArrayOf(0))
                        .build()


                mNotifyMgr.notify(MESSAGING_NOTIFICATION_ID, notification)

            }
            NEW_MATCHES -> {
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

    companion object {
        const val TYPE = "TYPE"
        const val CHAT_ID = "CHAT_ID"
        const val MESSAGE_ID = "MESSAGE_ID"
        const val MATCHES_CHANNELL = "MATCHES_CHANNEL"
        const val MESSAGING_CHANNELL = "MESSAGING_CHANNEL"
        const val MESSAGING_NOTIFICATION_ID = 3433
        const val SHARE_JOIN = "SHARE_JOIN"
        const val SHARE_COMPLETED = "SHARE_COMPLETED"
        const val SHARE_LEAVED = "SHARE_LEAVED"
        const val SHARE_CANCELED = "SHARE_CANCELED"
        const val SHARE_BAN = "SHARE_BAN"
        const val NEW_MESSAGE_RECEIVED = "NEW_MESSAGE"
        const val NEW_MATCHES = "NEW_MATCHES"
    }

}

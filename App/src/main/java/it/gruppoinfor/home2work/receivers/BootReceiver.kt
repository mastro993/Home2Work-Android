package it.gruppoinfor.home2work.receivers

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.pixplicity.easyprefs.library.Prefs

import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.SplashActivity
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.user.Const
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.model.User

class BootReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    private val sessionCallback: SessionManager.SessionCallback
        get() = object : SessionManager.SessionCallback {
            override fun onValidSession(user: User) {

                if (Prefs.getBoolean(Const.PREF_ACTIVITY_TRACKING, true)) {
                    val locationIntent = Intent(context, LocationService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(locationIntent)
                    } else {
                        context.startService(locationIntent)
                    }
                } else {
                    showEnableTrackingNotification()
                }
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        this.context = context
        SessionManager.loadSession(context, sessionCallback)
    }


    private fun showLoginNotification() {
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelID = "ROUTE_SERVICE_NOTIFICATION"
        val notificationIcon = R.drawable.home2work_icon

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, channelID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(icon)
                .setContentTitle("Home2Work")
                .setContentText("Per poter utilizzare il servizio devi effettuare l'accesso")
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)

    }

    private fun showEnableTrackingNotification() {

        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelID = "ROUTE_SERVICE_NOTIFICATION"
        val notificationIcon = R.drawable.home2work_icon

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(context, channelID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(icon)
                .setContentTitle("Tracking Attività disabilitato")
                .setContentText("Clicca qui per abilitare il tracking delle attività")
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)

    }

}

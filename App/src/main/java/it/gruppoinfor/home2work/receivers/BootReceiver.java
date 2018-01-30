package it.gruppoinfor.home2work.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import org.jetbrains.annotations.Nullable;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;

public class BootReceiver extends BroadcastReceiver implements SessionManager.SessionCallback {

    private Context mContext;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, Intent arg1) {
        mContext = context;
        SessionManager.loadSession(context, this);
    }

    @Override
    public void onValidSession() {
        // Carica le preferenze
        UserPrefs.init(mContext);

        // Servizio di localizzazione
        if (UserPrefs.TrackingEnabled) {
            Intent locationIntent = new Intent(mContext, LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(locationIntent);
            } else {
                mContext.startService(locationIntent);
            }
        } else {
            showEnableTrackingNotification();
        }
    }

    @Override
    public void onInvalidSession(int code, @Nullable Throwable throwable) {
        if (throwable != null) throwable.printStackTrace();
    }

    private void showLoginNotification() {
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelID = "ROUTE_SERVICE_NOTIFICATION";
        int notificationIcon = R.drawable.home2work_icon;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(mContext, channelID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                //.setLargeIcon(icon)
                .setContentTitle("Home2Work")
                .setContentText("Per poter utilizzare il servizio devi effettuare l'accesso")
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) notificationManager.notify(0, notification);
    }

    private void showEnableTrackingNotification() {
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelID = "ROUTE_SERVICE_NOTIFICATION";
        int notificationIcon = R.drawable.home2work_icon;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(mContext, channelID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                //.setLargeIcon(icon)
                .setContentTitle("Tracking Attività disabilitato")
                .setContentText("Clicca qui per abilitare il tracking delle attività")
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) notificationManager.notify(0, notification);
    }

}

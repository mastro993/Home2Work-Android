package it.gruppoinfor.home2work.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2workapi.Home2WorkClient;

public class MessagingService extends FirebaseMessagingService {

    private static final String TYPE = "TYPE";
    private static final String SHARE_JOIN = "SHARE_JOIN";
    public static final String SHARE_JOIN_REQUEST = "SHARE_JOIN_REQUEST";

    private LocalBroadcastManager broadcaster;

    private Map<String, String> data;
    private RemoteMessage.Notification notification;
    private PendingIntent pendingIntent;

    public MessagingService() {

    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            processData(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            notification = remoteMessage.getNotification();
            sendNotification();
        }

    }

    private void processData(Map<String, String> data) {
        if (data.get(TYPE) != null) {
            if (data.get(TYPE).equals(SHARE_JOIN)) {
                Intent intent = new Intent(SHARE_JOIN_REQUEST);
                broadcaster.sendBroadcast(intent);
            }
        }
    }

    private void sendNotification() {

        if (Home2WorkClient.User != null) {
            Intent resultIntent = new Intent(this, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent resultIntent = new Intent(this, SplashActivity.class);
            pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "BOOKING_NOTIFICATION_CHANNEL")
                        .setSmallIcon(R.drawable.home2work_icon)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);


        int mNotificationId = 32425;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel_01", "Canale prenotazioni", NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyMgr.createNotificationChannel(channel);
        }

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}

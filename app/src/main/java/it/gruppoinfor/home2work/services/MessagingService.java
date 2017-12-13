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
import it.gruppoinfor.home2work.activities.RequestActivity;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2workapi.Client;

public class MessagingService extends FirebaseMessagingService {

    private static final String TYPE = "TYPE";
    private static final String SHARE_TOJOB = "SHARE_TOJOB";
    private static final String SHARE_TOHOME = "SHARE_TOHOME";
    private static final String SHARE_ARRIVED = "SHARE_ARRIVED";
    private static final String SHARE_COMPLETED = "SHARE_COMPLETED";
    public static final String SHARE_ID = "SHARE_ID";
    public static final String SHARE_BOOKING_ID = "SHARE_BOOKING_ID";

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

    private void processData(Map<String, String> data){
        if(data.get(TYPE) != null){
            if(data.get(TYPE).equals(SHARE_TOJOB)){
                Long shareId = Long.parseLong(data.get(SHARE_ID));
                Long bookingId = Long.parseLong(data.get(SHARE_BOOKING_ID));

                Intent intent = new Intent("REQUEST_SHARE_START");
                intent.putExtra(SHARE_ID, shareId);
                intent.putExtra(SHARE_BOOKING_ID, bookingId);
                broadcaster.sendBroadcast(intent);
            }
        }
    }

    private void sendNotification() {

        if (Client.getSignedUser() != null) {
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
            NotificationChannel channel = new NotificationChannel("my_channel_01","Canale prenotazioni", NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyMgr.createNotificationChannel(channel);
        }

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}

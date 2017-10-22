package it.gruppoinfor.home2work.receivers;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.UserPrefs;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.User;
import it.gruppoinfor.home2work.services.RouteService;


public class BootReceiver extends BroadcastReceiver {

    private Context context;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, Intent arg1) {
        this.context = context;

        //Controllo se esiste una sessione
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.checkSession(new SessionManager.SessionManagerCallback() {
            @Override
            public void onValidSession(User user) {
                Client.setSignedUser(user);

                // Carica le preferenze
                UserPrefs.init(context);

                // Servizio di localizzazione
                if (UserPrefs.activityTrackingEnabled) {
                    Intent locationIntent = new Intent(context, RouteService.class);
                    context.startService(locationIntent);
                } else {
                    showEnableTrackingNotification();
                }

            }

            @Override
            public void onInvalidSession(SessionManager.AuthCode code) {
                showLoginNotification();
            }

        });

    }

    private void showLoginNotification() {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Azione richiesta")
                .setContentText("Per poter usare il servizio devi effettuare il login")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void showEnableTrackingNotification() {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tracking Attività disabilitato")
                .setContentText("Clicca qui per abilitare il tracking delle attività")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}

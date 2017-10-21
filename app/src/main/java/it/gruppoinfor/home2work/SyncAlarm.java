package it.gruppoinfor.home2work;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import it.gruppoinfor.home2work.services.RoutePointSync;

/**
 * Created by Federico on 15/02/2017.
 * <p>
 * AlarmManager per la gestione della sincronizzazione
 */

public class SyncAlarm extends BroadcastReceiver {

    public static final String TAG = SyncAlarm.class.getSimpleName();

    public SyncAlarm() {
    }

    public static void set(Context context) {
        MyLogger.d(TAG, "set");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SyncAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pi);
    }

    public static void remove(Context context) {
        MyLogger.d(TAG, "remove");
        Intent intent = new Intent(context, SyncAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLogger.d(TAG, "onReceive");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();

        RoutePointSync.sync(context);

        wl.release();
    }
}

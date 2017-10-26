package it.gruppoinfor.home2work.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2work.services.SyncService;

/**
 * Created by Federico on 15/02/2017.
 * <p>
 * AlarmManager per la gestione della sincronizzazione
 */

public class SyncAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "SYNC_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLogger.d(TAG, "Attivato");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();

        Intent locationIntent = new Intent(context, SyncService.class);
        context.startService(locationIntent);

        wl.release();
    }
}

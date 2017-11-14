package it.gruppoinfor.home2work.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.gruppoinfor.home2work.services.SyncService;
import it.gruppoinfor.home2work.utils.SessionManager;

/**
 * Created by Federico on 15/02/2017.
 * <p>
 * AlarmManager per la gestione della sincronizzazione
 */

public class SyncAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "SYNC_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent locationIntent = new Intent(context, SyncService.class);

        SessionManager sessionManager = new SessionManager(context);
        sessionManager.checkSession(new SessionManager.SessionManagerCallback() {
            @Override
            public void onValidSession() {
                context.startService(locationIntent);
            }

            @Override
            public void onInvalidSession(SessionManager.AuthCode code) {

            }

        });

        /*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();


        wl.release();*/
    }
}

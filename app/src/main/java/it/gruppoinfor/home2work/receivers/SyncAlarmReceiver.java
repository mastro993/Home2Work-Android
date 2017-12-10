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
        Intent syncIntent = new Intent(context, SyncService.class);

        SessionManager sessionManager = new SessionManager(context);
        sessionManager.checkSession(new SessionManager.SessionManagerCallback() {
            @Override
            public void onValidSession() {
                context.startService(syncIntent);
            }

            @Override
            public void onInvalidSession(SessionManager.AuthCode code) {
                super.onInvalidSession(code);
            }
        });
    }
}

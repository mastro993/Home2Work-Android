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

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent syncIntent = new Intent(context, SyncService.class);
        context.startService(syncIntent);
    }
}

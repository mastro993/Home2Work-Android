package it.gruppoinfor.home2work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import it.gruppoinfor.home2work.tracking.SyncService

/**
 * Created by Federico on 15/02/2017.
 *
 *
 * AlarmManager per la gestione della sincronizzazione
 */

class SyncAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val syncIntent = Intent(context, SyncService::class.java)
        context.startService(syncIntent)

    }
}

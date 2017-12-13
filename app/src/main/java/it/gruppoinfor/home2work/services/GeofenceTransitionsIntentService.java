package it.gruppoinfor.home2work.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2workapi.model.Share;

public class GeofenceTransitionsIntentService extends IntentService {

    private final String TAG = "GEOFENCE_SERVICE";
    private Share ongoingShare;
    private GeofencingEvent geofencingEvent;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            MyLogger.d(TAG, "Errore Geofence. Codice: " + geofencingEvent.getErrorCode());
            return;
        }

        //checkForOngoingShares();
        testGeofenceEvent();

    }


    private void testGeofenceEvent() {

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            for (Geofence geofence : triggeringGeofences) {
                MyLogger.d(TAG, geofence.getRequestId() + " -> INGRESSO");
            }

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            for (Geofence geofence : triggeringGeofences) {
                MyLogger.d(TAG, geofence.getRequestId() + " -> USCITA");
            }

        }
    }

}

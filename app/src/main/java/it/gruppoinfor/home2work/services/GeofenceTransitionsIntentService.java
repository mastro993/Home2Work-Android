package it.gruppoinfor.home2work.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.MyLogger;

public class GeofenceTransitionsIntentService extends IntentService {

    private final String TAG = "GEOFENCE_SERVICE";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            MyLogger.d(TAG, "ERRORE: " + geofencingEvent.getErrorCode());
            return;
        }

        // TODO analizzare geofence

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // TODO geofence

        } else {
            MyLogger.d(TAG, "tipo transizione non valida: " + geofenceTransition);
        }
    }

}

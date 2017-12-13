package it.gruppoinfor.home2work.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import it.gruppoinfor.home2work.services.GeofenceTransitionsIntentService;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.User;

public class GeofenceUtils {

    private static final float GEOFENCE_RADIUS = 1000f;
    public static final String HOME_GEOFENCE = "HOME_GEOFENCE";
    public static final String JOB_GEOFENCE = "JOB_GEOFENCE";

    public static void setupGeofences(Context context) {

        LatLng homeLocation = Client.User.getLocation();
        LatLng jobLocation = Client.User.getCompany().getLocation();

        GeofencingClient mGeofencingClient = LocationServices.getGeofencingClient(context);
        List<Geofence> geofenceList = new ArrayList<>();

        geofenceList.add(new Geofence.Builder()
                .setRequestId(HOME_GEOFENCE)
                .setCircularRegion(homeLocation.latitude, homeLocation.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        geofenceList.add(new Geofence.Builder()
                .setRequestId(JOB_GEOFENCE)
                .setCircularRegion(jobLocation.latitude, jobLocation.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        PendingIntent mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGeofencingClient.addGeofences(builder.build(), mGeofencePendingIntent)
                    .addOnSuccessListener(aVoid -> MyLogger.d("GEOFENCE", "Geofences registrate"))
                    .addOnFailureListener(e -> MyLogger.d("GEOFENCE", "Geofences NON registrate"));
        }

    }
}

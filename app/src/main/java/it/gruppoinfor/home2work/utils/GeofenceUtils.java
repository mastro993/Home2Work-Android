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
import java.util.concurrent.TimeUnit;
import it.gruppoinfor.home2work.services.GeofenceTransitionsIntentService;

public class GeofenceUtils {

    private static final float GEOFENCE_RADIUS = 1000f;

    public static void setupGeofence(Context context, String ID, LatLng latLng, int transitionTypes) {
        GeofencingClient mGeofencingClient = LocationServices.getGeofencingClient(context);

        Geofence geofence = new Geofence.Builder()
                .setRequestId(ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(TimeUnit.HOURS.toMillis(12))
                .setTransitionTypes(transitionTypes)
                .build();

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);


        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        PendingIntent mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGeofencingClient.addGeofences(builder.build(), mGeofencePendingIntent)
                    .addOnSuccessListener(aVoid -> MyLogger.d("GEOFENCE", "Registrata per luogo di lavoro"))
                    .addOnFailureListener(e -> MyLogger.d("GEOFENCE", "NON registrata per luogo di lavoro"));
        }


    }
}

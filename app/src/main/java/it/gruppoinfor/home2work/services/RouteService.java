package it.gruppoinfor.home2work.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.gruppoinfor.home2work.DrivingActivity;
import it.gruppoinfor.home2work.MyLogger;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.Tools;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.models.User;

import static it.gruppoinfor.home2work.App.dbApp;

public class RouteService extends Service {

    public final static String TAG = RouteService.class.getSimpleName();

    private final int LOCATION_INTERVAL = 10000; // Intervallo aggiornamento posizione 5 secondi
    private final float DISTANCE = 100f; // 50 metri
    private final int ACTIVITY_INTERVAL = 15000; // Intervallo aggiornamento attivita' 15 secondi
    private final long STOP_INTERVAL = 900000; // msec = 15 minuti
    private final int NOTIFICATION_ID = 1337;    // ID notifica
    private final int MIN_ROUTEPOINTS = 5; // Nuimero minimo di punti per poter salvare uin percorso
    private final String CHANNEL_ID = "ROUTE_SERVICE_NOTIFICATION";

    LocationRequest locationRequest;
    private boolean isRecording = false;
    private MyLocationListener routeLocationListener;
    private GoogleApiClient activityClient;
    private GoogleApiClient locationClient;
    private User user;

    private final boolean DEBUG_MODE = false;

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RouteService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.d(TAG, "onCreate");

        SessionManager sessionManager = new SessionManager(this);
        user = sessionManager.loadSession();

        if (user != null) {
            startService();
            //Toasty.success(this, "Servizio FleetUp avviato\nUtente collegato: " + user.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            //Toasty.error(this, "Errore durante l'avvio del servizio FleetUp", Toast.LENGTH_LONG).show();
        }

    }

    private void startService() {
        // Setup API Client
        if (activityClient == null) {
            MyLogger.d(TAG, "Creazione ActivityClient");
            activityClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new ActivityClientCallbacks())
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            MyLogger.w("ActivityAPIClient", "onConnectionFailed: " + connectionResult);
                        }
                    })
                    .addApi(ActivityRecognition.API)
                    .build();
        }
        if (locationClient == null) {
            MyLogger.d(TAG, "Creazione LocationClient");
            locationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new LocationClientCallbacks())
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            MyLogger.w("LocationAPIClient", "onConnectionFailed: " + connectionResult);
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }


        if(DEBUG_MODE) locationClient.connect();
        else activityClient.connect();

        // Avvio servizio in foreground
        startForeground(NOTIFICATION_ID, getNotification("Home2Work"));
    }

    private Notification getNotification(String title) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Tocca per aprire l'applicazione")
                .setPriority(Notification.PRIORITY_MIN)
                //.setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (ActivityRecognizedService.hasResult(intent)) {
            DrivingActivity drivingActivity = ActivityRecognizedService.extractResult(intent);
            if (drivingActivity == DrivingActivity.STARTED_DRIVING && !isRecording) {
                locationClient.connect();
            } else if (drivingActivity == DrivingActivity.STOPPED_DRIVING) {
                stopLocationRequests();
            }
        }

        return Service.START_STICKY;
    }

    private void startLocationRequests() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MyLogger.d(TAG, "startLocationRequests");

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(LOCATION_INTERVAL);
            locationRequest.setSmallestDisplacement(DISTANCE);

            routeLocationListener = new MyLocationListener();

            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, routeLocationListener);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, getNotification("Home2Work - Tracking in corso"));

            isRecording = true;
        }
    }

    private void stopLocationRequests() {
        MyLogger.d(TAG, "stopLocationRequests");

        if (locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, routeLocationListener);
            locationClient.disconnect();
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, getNotification("FleetUp"));

        isRecording = false;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
    }


    private class ActivityClientCallbacks implements GoogleApiClient.ConnectionCallbacks {

        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            MyLogger.d(TAG, "onConnected");

            Intent intent = new Intent(
                    RouteService.this,
                    ActivityRecognizedService.class
            );
            PendingIntent pendingIntent = PendingIntent.getService(
                    RouteService.this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    activityClient,
                    ACTIVITY_INTERVAL,
                    pendingIntent
            );
        }

        @Override
        public void onConnectionSuspended(int i) {
            MyLogger.w(TAG, "onConnectionSuspended: " + i);
        }

    }

    private class LocationClientCallbacks implements GoogleApiClient.ConnectionCallbacks {

        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            MyLogger.d(TAG, "onConnected");
            startLocationRequests();
        }

        @Override
        public void onConnectionSuspended(int i) {
            MyLogger.w(TAG, "onConnectionSuspended: " + i);
        }

    }

    private class MyLocationListener implements LocationListener {

        private final int MIN_LOCATIONS = 4; // Posizioni minime da avere per poter fare la stima della posizione migliore
        private ArrayList<Location> lastLocations = new ArrayList<>();

        @Override
        public void onLocationChanged(Location location) {
            lastLocations.add(location);
            if (lastLocations.size() >= MIN_LOCATIONS) {
                Location bestLocation = getBestLocation();

                final RoutePointEntity routePointEntity = new RoutePointEntity();
                LatLng latLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                routePointEntity.setLatLng(latLng);
                routePointEntity.setTimestamp(Tools.getCurrentTimestamp());
                routePointEntity.setUserId(Client.getSignedUser().getId());

                AsyncJob.doInBackground(() -> {
                    dbApp.routePointDAO().insert(routePointEntity);
                    MyLogger.d(TAG, "Posizione aggiunta (" + routePointEntity.getLatLng().toString() + ")");
                });

            }

        }

        private Location getBestLocation() {
            Location bestLocation = null;
            for (Location location : lastLocations) {
                if (bestLocation == null) bestLocation = location;
                else {
                    if (bestLocation.getAccuracy() > location.getAccuracy())
                        bestLocation = location;
                }
            }
            lastLocations.clear();
            return bestLocation;
        }
    }


}





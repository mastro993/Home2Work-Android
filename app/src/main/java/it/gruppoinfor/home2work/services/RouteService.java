package it.gruppoinfor.home2work.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.receivers.SyncAlarmReceiver;
import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.Client;

import static it.gruppoinfor.home2work.App.dbApp;

public class RouteService extends Service {

    private int NOTIFICATION_ID = 1337;
    private String TAG = "ROUTE_SERVICE";
    private boolean isRecording = false;
    private MyLocationListener routeLocationListener;
    private GoogleApiClient mGoogleApiClient;
    private Notification idleNotification;
    private Notification trackingNotification;
    private Location lastKnownLocation;
    private ArrayList<Location> lastLocations = new ArrayList<>();
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            MyLogger.d("GOOGLE_API_CLIENT", "Connessione avvenuta");

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
                    mGoogleApiClient,
                    10000,
                    pendingIntent
            );

        }

        @Override
        public void onConnectionSuspended(int i) {
            MyLogger.w(TAG, "onConnectionSuspended: " + i);
        }
    };

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

        String channelID = "ROUTE_SERVICE_NOTIFICATION";
        int notificationIcon = R.drawable.home2work_icon;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_marker_start);

        idleNotification = new NotificationCompat.Builder(this, channelID)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                //.setLargeIcon(icon)
                .setContentTitle("Home2Work")
                .setContentText("Servizio di localizzazione")
                .setOngoing(true)
                .setShowWhen(false)
                .build();

        trackingNotification = new NotificationCompat.Builder(this, channelID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(notificationIcon)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                //.setLargeIcon(icon)
                .setContentTitle("Home2Work - Tracking in corso")
                .setContentText("Servizio di localizzazione")
                .setOngoing(true)
                .setShowWhen(false)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener((connectionResult -> {
                    MyLogger.d("GOOGLE_API_CLIENT", "Connessione fallita: " + connectionResult);
                }))
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();

        if (Client.getSignedUser() != null) {

            MyLogger.i(TAG, "Sessione presente. Utente: " + Client.getSignedUser().getEmail());
            MyLogger.i(TAG, "Avvio servizio tracking");
            mGoogleApiClient.connect();

            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(this, SyncAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            am.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HOUR,
                    pi);

            startForeground(NOTIFICATION_ID, idleNotification);

        } else {
            MyLogger.i(TAG, "Sessione non presente");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (ActivityRecognizedService.hasResult(intent)) {
            DrivingActivity drivingActivity = ActivityRecognizedService.extractResult(intent);
            if (drivingActivity == DrivingActivity.STARTED_DRIVING && !isRecording) {
                startLocationRequests();
            } else if (drivingActivity == DrivingActivity.STOPPED_DRIVING) {
                stopLocationRequests();
            }
        }

        return Service.START_STICKY;
    }

    private void startLocationRequests() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            MyLogger.d(TAG, "Avvio tracking utente");

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    MyLogger.d(TAG, "Salvataggio posizione di partenza: " + location.getLatitude() + "," + location.getLongitude());
                } else {
                    MyLogger.d(TAG, "Posizione di partenza non disponibile");
                }
            });

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30000);
            locationRequest.setFastestInterval(15000);
            locationRequest.setSmallestDisplacement(250f);

            routeLocationListener = new MyLocationListener();

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, routeLocationListener);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, trackingNotification);

            isRecording = true;

        }
    }

    private void stopLocationRequests() {
        MyLogger.d(TAG, "Arresto tracking utente");

        if (lastKnownLocation != null && lastLocations.size() != 0) {
            MyLogger.d(TAG, "Salvataggio posizione di sosta: " + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
            saveLocation(lastKnownLocation);
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, routeLocationListener);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, idleNotification);

        isRecording = false;
    }

    private void saveLocation(Location location) {
        final RoutePointEntity routePointEntity = new RoutePointEntity();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        routePointEntity.setLatLng(latLng);
        routePointEntity.setTimestamp(Tools.getCurrentTimestamp());
        routePointEntity.setUserId(Client.getSignedUser().getId());

        AsyncJob.doInBackground(() -> {
            dbApp.routePointDAO().insert(routePointEntity);
            MyLogger.d(TAG, "Posizione aggiunta (" + routePointEntity.getLatLng().toString() + ")");
        });
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class MyLocationListener implements LocationListener {

        private final int MIN_LOCATIONS = 3;

        @Override
        public void onLocationChanged(Location location) {
            lastKnownLocation = location;
            lastLocations.add(location);
            if (lastLocations.size() >= MIN_LOCATIONS) {
                Location bestLocation = getBestLocation();
                saveLocation(bestLocation);
            }

        }

        private Location getBestLocation() {
            Location bestLocation = null;
            for (Location location : lastLocations) {
                if (bestLocation == null) bestLocation = location;
                else {
                    if (isBetterLocation(location, bestLocation))
                        bestLocation = location;
                }
            }
            lastLocations.clear();
            return bestLocation;
        }

        private boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(location.getProvider(),
                    currentBestLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true;
            } else if (!isLessAccurate) {
                return true;
            } else if (!isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
            return false;
        }


        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }


    }


}





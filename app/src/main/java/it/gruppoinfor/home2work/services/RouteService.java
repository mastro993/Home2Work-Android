package it.gruppoinfor.home2work.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.models.User;

import static it.gruppoinfor.home2work.App.dbApp;

public class RouteService extends Service {

    private boolean DEBUG_MODE = false;
    private int NOTIFICATION_ID = 1337;
    private String TAG = "ROUTE_SERVICE";
    private boolean isRecording = false;
    private MyLocationListener routeLocationListener;
    private GoogleApiClient activityClient;
    private GoogleApiClient locationClient;
    private GoogleApiClient awarenesClient;
    private User user;
    private Notification idleNotification;
    private Notification trackingNotification;
/* /////   */
    private PendingIntent myPendingIntent;
    private MyFenceReceiver myFenceReceiver;
    private AwarenessFence drivingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
    private String FENCE_RECEIVER_ACTION = "boh";

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
        MyLogger.i(TAG, "Creazione servizio");

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

        activityClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new ActivityClientCallbacks())
                .addOnConnectionFailedListener((connectionResult -> {
                    MyLogger.d("ACTIVITY_CLIENT", "Connessione fallita: " + connectionResult);
                }))
                .addApi(ActivityRecognition.API)
                .build();

        locationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new LocationClientCallbacks())
                .addOnConnectionFailedListener((connectionResult -> {
                    MyLogger.w("LOCATION_CLIENT", "Connessione fallita: " + connectionResult);
                }))
                .addApi(LocationServices.API)
                .build();


        // WHAT

        awarenesClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener((connectionResult -> {
                    MyLogger.d("AWARENESS_CLIENT", "Connessione fallita: " + connectionResult);
                }))
                .addApi(Awareness.API).build();
        awarenesClient.connect();

        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        PendingIntent myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        MyFenceReceiver myFenceReceiver = new MyFenceReceiver();
        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        Awareness.FenceApi.updateFences(
                awarenesClient,
                new FenceUpdateRequest.Builder()
                        .addFence("headphoneFenceKey", stillFence, myPendingIntent)
                        .build())
                .setResultCallback((status)->{
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence was successfully registered.");
                    } else {
                        Log.e(TAG, "Fence could not be registered: " + status);
                    }
                });


        // WHAT



        SessionManager sessionManager = new SessionManager(this);
        user = sessionManager.loadSession();

        if (user != null) {
            MyLogger.i(TAG, "Sessione presente. Utente: " + user.getEmail());
            startService();
        } else {
            MyLogger.i(TAG, "Sessione non presente");
        }

    }

    private void startService() {
        MyLogger.i(TAG, "Avvio servizio");

        if (DEBUG_MODE) locationClient.connect();
        else activityClient.connect();

        // Avvio servizio in foreground
        startForeground(NOTIFICATION_ID, idleNotification);
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
            MyLogger.d(TAG, "Tracking utente avviato");

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setSmallestDisplacement(100f);

            routeLocationListener = new MyLocationListener();

            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, routeLocationListener);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, trackingNotification);

            isRecording = true;
        }
    }

    private void stopLocationRequests() {
        MyLogger.d(TAG, "Tracking utente arrestato");

        if (locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, routeLocationListener);
            locationClient.disconnect();
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, idleNotification);

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

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            MyLogger.d("ACTIVITY_CLIENT", "Connessione avvenuta");

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
                    10000,
                    pendingIntent
            );

        }

        @Override
        public void onConnectionSuspended(int i) {
            MyLogger.w(TAG, "onConnectionSuspended: " + i);
        }

    }

    private class LocationClientCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            MyLogger.d("LOCATION_CLIENT", "Connessione avvenuta");
            startLocationRequests();
        }

        @Override
        public void onConnectionSuspended(int i) {
            MyLogger.w(TAG, "onConnectionSuspended: " + i);
        }

    }

    private class MyLocationListener implements LocationListener {

        private final int MIN_LOCATIONS = 3;
        private ArrayList<Location> lastLocations = new ArrayList<>();

        @Override
        public void onLocationChanged(Location location) {
            lastLocations.add(location);
            if (lastLocations.size() >= MIN_LOCATIONS || DEBUG_MODE) {
                Location bestLocation = getBestLocation();

                final RoutePointEntity routePointEntity = new RoutePointEntity();
                LatLng latLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                routePointEntity.setLatLng(latLng);
                routePointEntity.setTimestamp(Tools.getCurrentTimestamp());
                routePointEntity.setUserId(user.getId());

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

    // Handle the callback on the Intent.
    public class MyFenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i(TAG, "Headphones are plugged in.");
                        break;
                    case FenceState.FALSE:
                        Log.i(TAG, "Headphones are NOT plugged in.");
                        break;
                    case FenceState.UNKNOWN:
                        Log.i(TAG, "The headphone fence is in an unknown state.");
                        break;
                }
            }
        }
    }


}





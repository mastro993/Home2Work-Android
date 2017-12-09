package it.gruppoinfor.home2work.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.receivers.SyncAlarmReceiver;
import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.Client;

import static it.gruppoinfor.home2work.App.dbApp;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private String TAG = "LOCATION_SERVICE";
    private boolean isRecording = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            saveLocation(locationResult.getLastLocation());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        String channelID = "LOCATION_SERVICE_NOTIFICATION_CHANNEL";
        int notificationIcon = R.drawable.home2work_icon;

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener((connectionResult -> {
                    MyLogger.d("GOOGLE_API_CLIENT", "Connessione fallita: " + connectionResult);
                }))
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();

        if (Client.getSignedUser() != null) {

            MyLogger.i(TAG, "Utente collegato: " + Client.getSignedUser().getEmail());
            mGoogleApiClient.connect();

            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(this, SyncAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            am.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HOUR,
                    pi);

            Notification serviceNotification = new NotificationCompat.Builder(this, channelID)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setSmallIcon(notificationIcon)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle("Home2Work")
                    .setContentText("Servizio di localizzazione")
                    .setOngoing(true)
                    .setShowWhen(false)
                    .build();

            startForeground(1337, serviceNotification);

        } else {
            MyLogger.i(TAG, "Sessione non presente");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MyLogger.d(TAG, "Connessione Google Client avvenuta");

        Intent intent = new Intent(
                LocationService.this,
                ActivityRecognizedService.class
        );
        PendingIntent pendingIntent = PendingIntent.getService(
                LocationService.this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(LocationService.this);
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(15000, pendingIntent);

        task.addOnSuccessListener(result -> MyLogger.d(TAG, "activityRecognitionClient avviato con successo"));

    }

    @Override
    public void onConnectionSuspended(int i) {
        MyLogger.w(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (ActivityRecognizedService.hasResult(intent)) {
            ActivityRecognizedService.DrivingActivity drivingActivity = ActivityRecognizedService.extractResult(intent);
            if (drivingActivity == ActivityRecognizedService.DrivingActivity.STARTED_DRIVING && !isRecording) {
                startLocationRequests();
            } else if (drivingActivity == ActivityRecognizedService.DrivingActivity.STOPPED_DRIVING) {
                stopLocationRequests();
            }
        }

        return Service.START_STICKY;
    }

    private void startLocationRequests() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MyLogger.d(TAG, "Avvio tracking utente");

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this::saveLocation);

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(30000);
            locationRequest.setSmallestDisplacement(1000f);

            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

            isRecording = true;
        }
    }

    private void stopLocationRequests() {
        MyLogger.d(TAG, "Arresto tracking utente");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this::saveLocation);
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

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
            MyLogger.d(TAG, "Posizione aggiunta:" + routePointEntity.getLatLng().toString());
        });
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}





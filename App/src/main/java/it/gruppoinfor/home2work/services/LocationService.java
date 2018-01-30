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
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.database.RoutePointRepo;
import it.gruppoinfor.home2work.receivers.SyncAlarmReceiver;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.LatLng;
import it.gruppoinfor.home2workapi.model.User;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = LocationService.class.getSimpleName();

    private boolean isRecording = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private User mUser;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            saveLocation(locationResult.getLastLocation());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager.loadSession(this, new SessionManager.SessionCallback() {
            @Override
            public void onValidSession() {
                mUser = HomeToWorkClient.getUser();
                startService();
            }

            @Override
            public void onInvalidSession(int code, @Nullable Throwable throwable) {
                if (throwable != null) throwable.printStackTrace();
            }
        });


    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

        task.addOnSuccessListener(result -> Log.d(TAG, "activityRecognitionClient avviato con successo"));

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended: " + i);
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

    private void startService() {

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener((connectionResult -> Log.e(TAG, "Connessione fallita: " + connectionResult)))
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, SyncAlarmReceiver.class),
                0
        );

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HOUR,
                    pendingIntent);
        }

        Notification serviceNotification = new NotificationCompat.Builder(this, "LOCATION_SERVICE_NOTIFICATION_CHANNEL")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSmallIcon(R.drawable.home2work_icon)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle("Home2Work")
                .setContentText("Servizio di localizzazione")
                .setOngoing(true)
                .setShowWhen(false)
                .build();

        startForeground(1337, serviceNotification);
    }

    private void startLocationRequests() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this::saveLocation);

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(TimeUnit.MINUTES.toMillis(1));
            locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(30));
            locationRequest.setSmallestDisplacement(500f);

            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

            isRecording = true;
        }
    }

    private void stopLocationRequests() {

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
        routePointEntity.setUserId(mUser.getId());

        RoutePointRepo routePointRepo = new RoutePointRepo(this);
        routePointRepo.insert(routePointEntity);
    }


}





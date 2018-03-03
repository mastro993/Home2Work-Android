package it.gruppoinfor.home2work.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.model.UserLocation
import it.gruppoinfor.home2work.receivers.SyncAlarmReceiver
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.common.LatLng
import java.util.concurrent.TimeUnit


class LocationService : Service(), GoogleApiClient.ConnectionCallbacks {

    private var isTracking = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            saveLocation(locationResult!!.lastLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession() {
                startService()
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        })

    }

    override fun onBind(arg0: Intent): IBinder? {

        return null
    }

    override fun onConnected(bundle: Bundle?) {

        val intent = Intent(
                this@LocationService,
                ActivityRecognizedService::class.java
        )

        val pendingIntent = PendingIntent.getService(
                this@LocationService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityRecognitionClient = ActivityRecognition.getClient(this@LocationService)
        val task = activityRecognitionClient.requestActivityUpdates(10000, pendingIntent)

        task.addOnSuccessListener { Log.d(TAG, "activityRecognitionClient avviato con successo") }

    }

    override fun onConnectionSuspended(i: Int) {

        Log.w(TAG, "onConnectionSuspended: " + i)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (ActivityRecognizedService.hasResult(intent)) {
            val drivingActivity = ActivityRecognizedService.extractResult(intent)
            if (drivingActivity == ActivityRecognizedService.DrivingActivity.STARTED_DRIVING && !isTracking) {
                startLocationRequests()
            } else if (drivingActivity == ActivityRecognizedService.DrivingActivity.STOPPED_DRIVING) {
                stopLocationRequests()
            }
        }

        return Service.START_STICKY
    }

    private fun startService() {

        val mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener { connectionResult -> Log.e(TAG, "Connessione fallita: " + connectionResult) }
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient.connect()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, SyncAlarmReceiver::class.java),
                0
        )

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_HOUR,
                pendingIntent)

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel() else ""

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val serviceNotification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.home2work_icon)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle("Home2Work")
                .setContentText("Servizio di localizzazione")
                .setShowWhen(false)
                .build()

        startForeground(NOTIFICATION_ID, serviceNotification)

    }

    private fun startLocationRequests() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener({
                this.saveLocation(it)
            })

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = TimeUnit.MINUTES.toMillis(1)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(30)
            locationRequest.smallestDisplacement = 500f

            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())

            Answers.getInstance().logCustom(CustomEvent("Inizio tracking posizione"))

            isTracking = true
        }

    }

    private fun stopLocationRequests() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.lastLocation.addOnSuccessListener({ this.saveLocation(it) })
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)

        Answers.getInstance().logCustom(CustomEvent("Fine tracking posizione"))

        isTracking = false

    }

    private fun saveLocation(location: Location) {

        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)

        val userLocation = UserLocation()
        val latLng = LatLng(location.latitude, location.longitude)
        userLocation.latLng = latLng
        userLocation.timestamp = System.currentTimeMillis() / 1000L
        userLocation.userId = HomeToWorkClient.user!!.id

        userLocationBox.put(userLocation)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {

        val channelId = "hometowork_location_service"
        val channelName = "Servizio di localizzazione HomeToWork"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId

    }


    companion object {
        private val TAG = LocationService::class.java.simpleName
        const val NOTIFICATION_ID = 2313
    }


}





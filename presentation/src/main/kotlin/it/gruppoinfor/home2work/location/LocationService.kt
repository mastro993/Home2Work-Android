package it.gruppoinfor.home2work.location

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.entities.UserLocation
import it.gruppoinfor.home2work.sync.SyncJobService
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class LocationService : Service() {

    companion object {
        const val NOTIFICATION_ID = 2313
        val EXTRA_USER_ID = "user_id"
    }

    private var userId: Long? = null

    private var isTracking = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            saveLocation(locationResult!!.lastLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()

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

        Timber.i("Servizio avviato con successo")

    }

    override fun onBind(arg0: Intent): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let {
            if (ActivityRecognizedService.hasResult(it)) {

                val drivingActivity = ActivityRecognizedService.extractResult(it)
                if (drivingActivity == ActivityRecognizedService.DrivingActivity.STARTED_DRIVING && !isTracking) {
                    startLocationRequests()
                } else if (drivingActivity == ActivityRecognizedService.DrivingActivity.STOPPED_DRIVING) {
                    stopLocationRequests()
                }

            } else if (it.hasExtra(LocationService.EXTRA_USER_ID)) {

                userId = intent.getLongExtra(LocationService.EXTRA_USER_ID, 0L)

                val mGoogleApiClient = GoogleApiClient.Builder(this@LocationService)
                        .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                            override fun onConnected(p0: Bundle?) {

                                Timber.i("Client Google connesso")
                                SyncJobService.schedule(this@LocationService, userId)
                                startActivityRecognitionService()

                            }

                            override fun onConnectionSuspended(p0: Int) {
                                Timber.w("Connessione sospesa. Codice: $p0")
                            }
                        })
                        .addOnConnectionFailedListener { connectionResult ->
                            Timber.e("Connessione fallita: $connectionResult")
                        }
                        .addApi(ActivityRecognition.API)
                        .addApi(LocationServices.API)
                        .build()
                mGoogleApiClient.connect()
            }
        }



        return Service.START_STICKY
    }

    private fun startActivityRecognitionService() {

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

        task.addOnSuccessListener {

            Timber.v("Activity Recognition Service avviato")

        }

    }

    private fun startLocationRequests() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = TimeUnit.MINUTES.toMillis(1)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(30)
            locationRequest.smallestDisplacement = 500f

            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())

            isTracking = true

            Timber.v("Inizio tracking utente")
        }

    }

    private fun stopLocationRequests() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.lastLocation.addOnSuccessListener({ this.saveLocation(it) })
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)

        isTracking = false

        Timber.v("Fine tracking utente")

    }

    private fun saveLocation(location: Location) {
       userId?.let {
           val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)

           val userLocation = UserLocation(
                   latitude = location.latitude,
                   longitude = location.longitude,
                   date = Date(),
                   userId = it
           )

           userLocationBox.put(userLocation)
       }

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


}





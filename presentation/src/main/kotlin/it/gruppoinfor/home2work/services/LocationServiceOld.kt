package it.gruppoinfor.home2work.services

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
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.mappers.UserLocationUserLocationEntityMapper
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.StoreUserLocation
import it.gruppoinfor.home2work.entities.UserLocation
import org.jetbrains.anko.startService
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LocationServiceOld : Service() {

    @Inject
    lateinit var saveUserLocation: StoreUserLocation
    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    companion object {
        const val NOTIFICATION_ID = 2313

        fun launch(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val locationIntent = Intent(context, LocationServiceOld::class.java)
                context.startForegroundService(locationIntent)
            } else {
                context.startService<LocationServiceOld>()
            }
        }
    }

    private var isTracking = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            saveLocation(locationResult!!.lastLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)

        localUserData.user?.let {

            val mGoogleApiClient = GoogleApiClient.Builder(this@LocationServiceOld)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                        override fun onConnected(p0: Bundle?) {
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

            Timber.i("Servizio avviato")

        } ?: let {
            Timber.v("Nessun utente collegato")
            stopSelf()
        }

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
            }
        }
        return Service.START_STICKY
    }

    private fun startActivityRecognitionService() {

        val intent = Intent(
                this@LocationServiceOld,
                ActivityRecognizedService::class.java
        )

        val pendingIntent = PendingIntent.getService(
                this@LocationServiceOld,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityRecognitionClient = ActivityRecognition.getClient(this@LocationServiceOld)
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
            locationRequest.interval = TimeUnit.MINUTES.toMillis(10)
            locationRequest.fastestInterval = TimeUnit.MINUTES.toMillis(1)
            locationRequest.smallestDisplacement = 2500f

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
        localUserData.user?.let {

            val userLocation = UserLocation(
                    userId = it.id,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = Date()
            )

            val userLocationEntity = UserLocationUserLocationEntityMapper().mapFrom(userLocation)
            saveUserLocation.save(userLocationEntity).subscribe({
                Timber.v("Posizione utente registrata: ${userLocation.latitude}, ${userLocation.longitude}")
            }, {
                Timber.e(it)
            })
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





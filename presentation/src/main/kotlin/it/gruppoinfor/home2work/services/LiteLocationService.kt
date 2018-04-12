package it.gruppoinfor.home2work.services

import android.Manifest
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
import com.google.android.gms.common.ConnectionResult
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
import javax.inject.Inject


/**
 * Versione Lite del servizio di localizzazione. Salva la posizione dell'utente solo quando inizia o finisce di guidare.
 * Questo comporta un numero minore di posizioni registrate per ogni utente.
 */
class LiteLocationService : Service(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    lateinit var storeUserLocation: StoreUserLocation
    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    companion object {
        const val NOTIFICATION_ID = 2313

        fun launch(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val locationIntent = Intent(context, LiteLocationService::class.java)
                context.startForegroundService(locationIntent)
            } else {
                context.startService<LiteLocationService>()
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            saveLocation(locationResult!!.lastLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)

        localUserData.user?.let {

            val mGoogleApiClient = GoogleApiClient.Builder(this@LiteLocationService)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
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

    override fun onConnected(bundle: Bundle?) {
        val intent = Intent(
                this@LiteLocationService,
                ActivityRecognizedService::class.java
        )

        val pendingIntent = PendingIntent.getService(
                this@LiteLocationService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityRecognitionClient = ActivityRecognition.getClient(this@LiteLocationService)
        val task = activityRecognitionClient.requestActivityUpdates(10000, pendingIntent)

        task.addOnSuccessListener {
            Timber.v("Activity Recognition Service avviato")
        }
    }

    override fun onConnectionSuspended(code: Int) {
        Timber.w("Connessione sospesa. Codice: $code")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Timber.e("Connessione fallita: $connectionResult")
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let {
            if (ActivityRecognizedService.hasResult(it)) {
                getUserLocation()
            }
        }
        return Service.START_STICKY
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    val lastLocation = locationResult.lastLocation

                    Timber.v("Posizione utente ottenuta: ${lastLocation.latitude}, ${lastLocation.longitude} (${lastLocation.accuracy})")

                    saveLocation(locationResult.lastLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }
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
            storeUserLocation.save(userLocationEntity).subscribe({
                Timber.v("Posizione utente registrata con successo")
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





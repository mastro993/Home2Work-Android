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
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.usecases.StoreUserLocation
import it.gruppoinfor.home2work.domain.usecases.SyncUserLastLocation
import it.gruppoinfor.home2work.entities.UserLocation
import org.jetbrains.anko.startService
import timber.log.Timber
import java.util.*
import javax.inject.Inject


/**
 * Servizio che si occupa di sincronizzare la posizione aggiornata dell'utente con il server e
 * di tracciare gli spostamenti casa lavoro dell'utente
 */
class LocationService : Service(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    lateinit var storeUserLocation: StoreUserLocation
    @Inject
    lateinit var syncUserLastLocation: SyncUserLastLocation
    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    private val lastLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val userLocation = UserLocationEntity(
                    userId = localUserData.user!!.id,
                    latitude = locationResult.lastLocation.latitude,
                    longitude = locationResult.lastLocation.longitude,
                    date = Date()
            )

            syncUserLastLocation.upload(userLocation)
                    .subscribe({
                        Timber.i("Posizione utente aggiornata: $userLocation")
                    }, {
                        Timber.e(it, "Impossibile aggiornare la posizione utente")
                    })

        }
    }
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    companion object {
        const val NOTIFICATION_ID = 2313
        const val REQ_ACTIVITY_UPDATES = 343
        const val TIME_ACTIVITY_UPDATES = 10000L // 10 sec

        fun launch(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val locationIntent = Intent(context, LocationService::class.java)
                context.startForegroundService(locationIntent)
            } else {
                context.startService<LocationService>()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        DipendencyInjector.mainComponent.inject(this)

        localUserData.user?.let {

            val mGoogleApiClient = GoogleApiClient.Builder(this@LocationService)
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

            Timber.w("Nessun utente collegato")
            stopSelf()

        }

    }

    override fun onConnected(bundle: Bundle?) {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val intent = Intent(this, ActivityRecognizedService::class.java)

        val pendingIntent = PendingIntent.getService(
                this@LocationService,
                REQ_ACTIVITY_UPDATES,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Avvio il servizio di riconoscimento delle attività dell'utente
        val activityRecognitionClient = ActivityRecognition.getClient(this@LocationService)
        val task = activityRecognitionClient.requestActivityUpdates(TIME_ACTIVITY_UPDATES, pendingIntent)
        task.addOnSuccessListener {
            Timber.v("Activity Recognition Service avviato")
        }

        // Avvio il listener per l'ultima posizione utente
        startUserLastLocationUpdates()

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
            // Richiedo un solo aggiornamento di posizione
            locationRequest.numUpdates = 1
            // Massima accuratezza possibile
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    val lastLocation = locationResult.lastLocation

                    Timber.v("User position: ${lastLocation.latitude}, ${lastLocation.longitude} (${lastLocation.accuracy})")

                    saveLocation(locationResult.lastLocation)
                    mFusedLocationProviderClient?.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }
    }

    private fun startUserLastLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            // Priorità al risparmio batteria
            locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            // Voglio la posizione non prima di uno spostamento di almeno 1 km dall'ultima posizione nota
            locationRequest.smallestDisplacement = 1000f
            // Se altre app hanno richiesto l'aggiornamento della posizine la ottengo anche io ma non prima che siano passati almeno 5 minuti
            locationRequest.fastestInterval = 5 * 60 * 1000
            // Se non disponibile, richiedo io un aggiornamento di posizione ogni 30 minuti
            locationRequest.interval = 30 * 60 * 1000

            mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, lastLocationCallback, Looper.myLooper())

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
                Timber.v("User position successfully saved")
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

    override fun onDestroy() {
        super.onDestroy()

        mFusedLocationProviderClient?.removeLocationUpdates(lastLocationCallback)

    }


}





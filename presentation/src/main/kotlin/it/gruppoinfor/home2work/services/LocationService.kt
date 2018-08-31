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
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.mappers.UserLocationUserLocationEntityMapper
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.SettingsRepository
import it.gruppoinfor.home2work.domain.usecases.StoreUserLocation
import it.gruppoinfor.home2work.domain.usecases.SyncUserLastLocation
import it.gruppoinfor.home2work.entities.UserLocation
import org.jetbrains.anko.doAsync
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
    lateinit var settingsRepository: SettingsRepository


    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var mActivityTransitionRecognition: ActivityRecognitionTransition
    private val lastLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Timber.d("onLocationResult: $locationResult")

            val userLocation = UserLocationEntity(
                    userId = localUserData.user!!.id,
                    latitude = locationResult.lastLocation.latitude,
                    longitude = locationResult.lastLocation.longitude,
                    date = Date()
            )

            syncUserLastLocation.upload(userLocation)
                    .subscribe({
                        Timber.i("Posizione utente aggiornata con successo")
                    }, {
                        if (it is RetrofitException) {
                            when (it.kind) {
                                RetrofitException.Kind.NETWORK -> Timber.i(it, "Impossibile aggiornare la posizione utente al momento") // TODO riprovare a caricare la posizione in seguito
                                else -> Timber.w(it, "Impossibile aggiornare la posizione utente")
                            }
                        } else {
                            Timber.w(it, "Impossibile aggiornare la posizione utente")
                        }
                    })

        }
    }


    companion object {
        const val NOTIFICATION_ID = 2313

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
        Timber.d("LocationService: %1s", "onCreate")

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

            Timber.i("Servizio avviato per l'utente ${it.email}")

        } ?: let {

            Timber.w("Nessun utente collegato")
            stopSelf()

        }

    }

    override fun onConnected(bundle: Bundle?) {
        Timber.d("LocationService client: %1s", "onConnected")

        // Avvio il servizio di riconoscimento delle attività dell'utente
        mActivityTransitionRecognition = ActivityRecognitionTransition()
        mActivityTransitionRecognition.startTracking(this)

        // Avvio il listener per l'ultima posizione utente
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startUserLastLocationUpdates()

    }

    override fun onConnectionSuspended(code: Int) {
        Timber.w("LocationService client: %1s", "onConnectionSuspended (code $code)")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Timber.e("LocationService client: %1s", "onConnectionFailed $connectionResult")
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("LocationService: %1s", "onStartCommand")

        intent?.let {
            if (ActivityRecognitionReceiver.hasResult(it)) {
                getUserLocation()
            }
        }

        return Service.START_STICKY
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1 // Richiedo un solo aggiornamento di posizione
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Massima accuratezza possibile

            mFusedLocationProviderClient?.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val lastLocation = locationResult.lastLocation
                    Timber.d("onLocationResult: ${lastLocation.latitude}, ${lastLocation.longitude} (${lastLocation.accuracy})")
                    saveLocation(locationResult.lastLocation)
                    mFusedLocationProviderClient?.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }
    }

    private fun startUserLastLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER // Priorità al risparmio batteria
            locationRequest.smallestDisplacement = 2000f // Spostamento di almeno 2 km dall'ultima posizione nota
            locationRequest.fastestInterval = 10 * 60 * 1000 // 10 Minuti
            locationRequest.interval = 30 * 60 * 1000 // 30 minuti

            mFusedLocationProviderClient
                    ?.requestLocationUpdates(locationRequest, lastLocationCallback, Looper.myLooper())
                    ?.addOnSuccessListener {
                        Timber.i("Rilevamento ultima posizione utente avviato")
                    }

        }
    }

    private fun saveLocation(location: Location) {
        localUserData.user?.let { it ->

            doAsync {
                val userLocation = UserLocation(
                        userId = it.id,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        date = Date()
                )

                val userLocationEntity = UserLocationUserLocationEntityMapper().mapFrom(userLocation)
                storeUserLocation.save(userLocationEntity).subscribe({
                    Timber.i("Posizione utente salvata con successo")
                }, {
                    Timber.w("Impossibile salvare posizione utente", it)
                })
            }

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
        Timber.d("LocationService: %1s", "onDestroy")

        mActivityTransitionRecognition.stopTracking()
        mFusedLocationProviderClient?.removeLocationUpdates(lastLocationCallback)
    }


}





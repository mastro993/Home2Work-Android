package it.gruppoinfor.home2work.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.usecases.SyncUserLastLocation
import org.jetbrains.anko.startService
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LastLocationService : Service(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    lateinit var syncUserLastLocation: SyncUserLastLocation
    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    companion object {

        fun launch(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val locationIntent = Intent(context, LastLocationService::class.java)
                context.startForegroundService(locationIntent)
            } else {
                context.startService<LastLocationService>()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        localUserData.user?.let {

            val mGoogleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()

            mGoogleApiClient.connect()

            Timber.i("Servizio avviato")

        } ?: let {

            Timber.w("Nessun utente collegato")
            stopSelf()

        }

        return START_STICKY
    }

    override fun onConnected(bundle: Bundle?) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER // Priorità al risparmio batteria
            locationRequest.smallestDisplacement = 1000f // 1km
            locationRequest.fastestInterval = 10 * 60 * 1000 // Ogni 5 minuti solo se disponibile
            locationRequest.interval = 30 * 60 * 1000 // Ogni 30 minuti

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    val userLocation = UserLocationEntity(
                            userId = localUserData.user!!.id,
                            latitude = locationResult.lastLocation.latitude,
                            longitude = locationResult.lastLocation.longitude,
                            date = Date()
                    )

                    syncUserLastLocation.upload(userLocation)
                            .subscribe({

                            }, {

                            })

                }
            }, Looper.myLooper())

        }

    }

    override fun onConnectionSuspended(code: Int) {
        Timber.w("Connessione sospesa. Codice: $code")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Timber.e("Connessione fallita: $connectionResult")
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}

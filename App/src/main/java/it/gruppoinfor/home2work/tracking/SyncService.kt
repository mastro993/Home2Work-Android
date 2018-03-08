package it.gruppoinfor.home2work.tracking

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.user.SettingsActivity
import it.gruppoinfor.home2work.utils.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.location.Location
import timber.log.Timber
import java.util.*


class SyncService : Service() {

    private val routeLocations = ArrayList<Location>()

    override fun onCreate() {
        super.onCreate()

        Timber.i("Avvio servizio di sincronizzazione")

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession() {
                if (canSync()) sync()
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {

                Timber.w(throwable, "Sessione non presente o non valida")

            }
        })

    }

    fun sync() {

        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
        val userLocations = userLocationBox.query().equal(UserLocation_.userId, HomeToWorkClient.user!!.id).build().find()

        userLocations.forEach {
            val routeLocation = Location()
            routeLocation.latLng = it.latLng
            routeLocation.date = Date(it.timestamp * 1000)
            routeLocations.add(routeLocation)
        }

        if (routeLocations.size > 0) syncRoutePoints(routeLocations)


    }

    private fun syncRoutePoints(locationList: List<Location>) {

        Timber.i("Sincronizzazione di ${locationList.size} posizioni utente")

        HomeToWorkClient.uploadLocations(locationList,
                OnSuccessListener {

                    Timber.i("Sincronizzazione completata")
                    Answers.getInstance().logCustom(CustomEvent("Sincronizzazione posizioni"))

                    val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
                    userLocationBox.query().equal(UserLocation_.userId, HomeToWorkClient.user!!.id).build().remove()

                },
                OnFailureListener { e ->

                    Timber.e(e, "Sincronizzazione fallita")

                })

    }

    private fun getConnectivityType(context: Context): Int {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) { // connected to the internet
                return activeNetwork.type
            }
        }

        return -1
    }

    private fun canSync(): Boolean {

        val wifiEnabled = getConnectivityType(this@SyncService) == ConnectivityManager.TYPE_WIFI

        return wifiEnabled || Prefs.getBoolean(SettingsActivity.PREFS_SYNC_WITH_DATA, true)
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}

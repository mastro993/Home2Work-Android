package it.gruppoinfor.home2work.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.model.UserLocation
import it.gruppoinfor.home2work.model.UserLocation_
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.RouteLocation
import it.gruppoinfor.home2workapi.model.User
import java.util.*


class SyncService : Service() {

    private val routeLocations = ArrayList<RouteLocation>()
    private lateinit var mUser: User

    override fun onCreate() {
        super.onCreate()

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession(user: User) {
                mUser = user
                if (canSync()) sync()
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        })

    }

    fun sync() {

        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
        val userLocations = userLocationBox.query().equal(UserLocation_.userId, HomeToWorkClient.user!!.id).build().find()

        userLocations.forEach {
            val routeLocation = RouteLocation()
            routeLocation.latLng = it.latLng
            routeLocation.date = Date(it.timestamp * 1000)
            routeLocations.add(routeLocation)
        }

        if (routeLocations.size > 0) syncRoutePoints(routeLocations)


    }

    private fun syncRoutePoints(routeLocationList: List<RouteLocation>) {

        HomeToWorkClient.getInstance().uploadLocation(mUser.id, routeLocationList,
                OnSuccessListener {

                    Answers.getInstance().logCustom(CustomEvent("Sincronizzazione posizioni"))

                    val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
                    userLocationBox.query().equal(UserLocation_.userId, HomeToWorkClient.user!!.id).build().remove()

                },
                OnFailureListener { e -> Log.e(this::class.java.name, "Sincronizzazione fallita", Throwable(e)) })

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

        return wifiEnabled || Prefs.getBoolean(Const.PREF_SYNC_WITH_DATA, true)
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}

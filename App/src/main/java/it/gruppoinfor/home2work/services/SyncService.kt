package it.gruppoinfor.home2work.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.IBinder
import android.util.Log

import java.util.ArrayList
import java.util.Date

import it.gruppoinfor.home2work.database.RoutePointEntity
import it.gruppoinfor.home2work.database.RoutePointRepo
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.user.UserPrefs
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.RouteLocation
import it.gruppoinfor.home2workapi.model.User


class SyncService : Service() {

    private val TAG = "SYNC_SERVICE"

    private val routeLocations = ArrayList<RouteLocation>()
    private var mRoutePointRepo: RoutePointRepo? = null
    private var mUser: User? = null

    override fun onCreate() {
        super.onCreate()

        UserPrefs.init(this)
        mRoutePointRepo = RoutePointRepo(this)

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession() {
                mUser = HomeToWorkClient.getUser()
                if (canSync()) sync()
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        })

    }

    fun sync() {

        mRoutePointRepo!!.getAllUserLocations({ routePointEntities ->
            for (routePointEntity in routePointEntities) {
                val routeLocation = RouteLocation()
                routeLocation.latLng = routePointEntity.latLng
                routeLocation.date = Date(routePointEntity.timestamp * 1000)
                routeLocations.add(routeLocation)
            }

            if (routeLocations.size > 0) syncRoutePoints(routeLocations)
        }, OnFailureListener { it.printStackTrace() })
    }

    private fun syncRoutePoints(routeLocationList: List<RouteLocation>) {
        HomeToWorkClient.getInstance().uploadLocation(mUser!!.id!!, routeLocationList,
                { locations -> mRoutePointRepo!!.deleteAllUserLocations(mUser!!.id!!) }
        ) { e -> Log.e(TAG, "Sincronizzazione fallita", Throwable(e)) }

    }

    private fun getConnectivityType(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) { // connected to the internet
                return activeNetwork.type
            }
        }
        return -1
    }

    private fun canSync(): Boolean {
        val WiFiEnabled = getConnectivityType(this@SyncService) == ConnectivityManager.TYPE_WIFI
        return WiFiEnabled || UserPrefs.SyncWithData
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

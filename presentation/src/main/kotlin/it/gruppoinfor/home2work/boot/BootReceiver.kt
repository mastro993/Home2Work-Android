package it.gruppoinfor.home2work.boot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.location.LocationService
import org.jetbrains.anko.startService
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var localUserData: LocalUserData


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        DipendencyInjector.mainComponent.inject(context)

        localUserData.user?.let {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val locationIntent = Intent(context, LocationService::class.java)
                locationIntent.putExtra(LocationService.EXTRA_USER_ID, it.id)
                context.startForegroundService(locationIntent)
            } else {
                context.startService<LocationService>(LocationService.EXTRA_USER_ID to it.id)
            }
        }

    }

}

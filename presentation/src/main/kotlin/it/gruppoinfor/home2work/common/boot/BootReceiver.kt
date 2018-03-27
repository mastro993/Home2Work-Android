package it.gruppoinfor.home2work.common.boot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import it.gruppoinfor.home2work.common.services.LocationService
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var localUserData: LocalUserData


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        DipendencyInjector.mainComponent.inject(context)

        localUserData.user?.let {
            LocationService.launch(context, it.id)
        }

    }

}

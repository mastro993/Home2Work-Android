package it.gruppoinfor.home2work.common.boot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import it.gruppoinfor.home2work.services.LiteLocationService

class BootReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        LiteLocationService.launch(context)
    }

}

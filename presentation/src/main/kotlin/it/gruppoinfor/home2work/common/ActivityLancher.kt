package it.gruppoinfor.home2work.common

import android.content.Context
import android.content.Intent


interface ActivityLancher {
    fun intent(activity: Context): Intent
    fun launch(activity: Context) = activity.startActivity(intent(activity))
}
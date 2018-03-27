package it.gruppoinfor.home2work.common.timber

import android.os.Build
import android.util.Log
import timber.log.Timber


class DebugLogTree : Timber.DebugTree() {
    override fun log(p: Int, tag: String?, message: String, t: Throwable?) {
        var priority = p
        // Workaround for devices that doesn't show lower priority logs
        if (Build.MANUFACTURER == "HUAWEI" || Build.MANUFACTURER == "samsung") {
            if (p == Log.VERBOSE || p == Log.DEBUG || p == Log.INFO)
                priority = Log.ERROR
        }
        super.log(priority, tag, message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }
}
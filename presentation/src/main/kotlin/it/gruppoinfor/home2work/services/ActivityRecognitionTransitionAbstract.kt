package it.gruppoinfor.home2work.services

import android.content.Context

abstract class ActivityRecognitionTransitionAbstract {
    abstract fun startTracking(context: Context)
    abstract fun stopTracking()
}
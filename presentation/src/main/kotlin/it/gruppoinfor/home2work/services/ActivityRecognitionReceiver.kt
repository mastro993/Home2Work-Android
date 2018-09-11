package it.gruppoinfor.home2work.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import org.jetbrains.anko.intentFor
import timber.log.Timber

class ActivityRecognitionReceiver : BroadcastReceiver() {

    lateinit var mContext: Context

    companion object {
        const val EXTRA_ACTIVITY = "activity"

        fun hasResult(intent: Intent): Boolean {
            return intent.hasExtra(EXTRA_ACTIVITY)
        }

        fun extractResult(intent: Intent): ActivityRecognitionReceiver.DrivingActivity {
            return intent.getSerializableExtra(EXTRA_ACTIVITY) as ActivityRecognitionReceiver.DrivingActivity
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context!!

        Timber.d("TransitionReceiver: %1s", "onReceive")

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)

            if (result != null) {
                processTransitionResult(result)
            }
        }
    }


    private fun processTransitionResult(result: ActivityTransitionResult) {
        for (event in result.transitionEvents) {
            onDetectedTransitionEvent(event)
        }
    }

    private fun onDetectedTransitionEvent(activity: ActivityTransitionEvent) {
        logDetectedActivity(activity)
        if (activity.activityType == DetectedActivity.IN_VEHICLE && activity.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            startDrivingActivity()
        } else if (activity.activityType == DetectedActivity.IN_VEHICLE && activity.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            stopDrivingActivity()
        }
    }

    private fun startDrivingActivity() {
        mContext.startService(mContext.intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.DRIVING_START))
        Timber.i("Guida iniziata")
    }

    private fun stopDrivingActivity() {
        mContext.startService(mContext.intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.DRIVING_STOP))
        Timber.i("Guida conclusa")
    }

    private fun logDetectedActivity(activity: ActivityTransitionEvent) {

        val type = when (activity.activityType) {
            DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
            DetectedActivity.ON_FOOT -> "ON_FOOT"
            else -> activity.activityType.toString()
        }

        val transition = when (activity.transitionType) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
            else -> "EXIT"
        }

        Timber.d("ActivityTransition: $type -> $transition")
    }

    public enum class DrivingActivity {
        DRIVING_START, DRIVING_STOP
    }

}
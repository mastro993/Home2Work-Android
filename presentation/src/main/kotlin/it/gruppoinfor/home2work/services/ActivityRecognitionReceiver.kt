package it.gruppoinfor.home2work.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import org.jetbrains.anko.intentFor
import timber.log.Timber

class ActivityRecognitionReceiver : BroadcastReceiver() {

    lateinit var mContext: Context

    companion object {
        const val EXTRA_ACTIVITY = "activity"
        private var IN_VEHICLE_FLAG = 0

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
        if (activity.activityType == DetectedActivity.IN_VEHICLE && IN_VEHICLE_FLAG == 0) {
            startDrivingActivity()
        } else if (activity.activityType == DetectedActivity.ON_FOOT && IN_VEHICLE_FLAG == 1) {
            stopDrivingActivity()
        }
    }

    private fun startDrivingActivity() {
        mContext.startService(mContext.intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.DRIVING))
        IN_VEHICLE_FLAG = 1
        Timber.i("User is driving")
    }

    private fun stopDrivingActivity() {
        mContext.startService(mContext.intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.NOT_DRIVING))
        IN_VEHICLE_FLAG = 0
        Timber.i("User finished driving")
    }

    enum class DrivingActivity {
        DRIVING, NOT_DRIVING
    }

}
package it.gruppoinfor.home2work.services

import android.app.IntentService
import android.content.Intent
import android.util.Log

import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity


class ActivityRecognizedService : IntentService("ActivityRecognizedService") {

    private var isDriving = false
    private var stillStatusCounter = 0

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            handleDetectedActivities(result.probableActivities)
        }
    }

    private fun handleDetectedActivities(probableActivities: List<DetectedActivity>) {
        for (activity in probableActivities) {
            //Log.d("DETECTED_ACTIVITY", activity.toString());
            if (activity.confidence >= CONFIDENCE_TRESHOLD) {
                if (activity.type == DetectedActivity.IN_VEHICLE) {
                    if (!isDriving) startDrivingActivity()
                    break
                } else if (activity.type == DetectedActivity.ON_FOOT || activity.type == DetectedActivity.ON_BICYCLE) {
                    if (isDriving) stopDrivingActivity()
                    break
                } else if (activity.type == DetectedActivity.STILL) {
                    if (isDriving) {
                        stillStatusCounter += 1
                        if (stillStatusCounter >= MAX_STILL_STATUS_COUNT)
                            stopDrivingActivity()
                    }
                    break
                }
            }
        }

    }

    private fun startDrivingActivity() {
        stillStatusCounter = 0
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(DrivingActivity::class.java.simpleName, DrivingActivity.STARTED_DRIVING)
        startService(intent)
        Log.d(TAG, "Auto rilevata")
        isDriving = true
    }

    private fun stopDrivingActivity() {
        stillStatusCounter = 0
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(DrivingActivity::class.java.simpleName, DrivingActivity.STOPPED_DRIVING)
        startService(intent)
        Log.d(TAG, "Auto non rilevata")
        isDriving = false
    }

    enum class DrivingActivity {
        STARTED_DRIVING, STOPPED_DRIVING
    }

    companion object {

        private const val CONFIDENCE_TRESHOLD = 80 // Valore minimo di affidabilita' per i trigger delle attivita'
        private const val TAG = "ACTIVITY_RECOGNITION"
        private const val MAX_STILL_STATUS_COUNT = 2

        fun hasResult(intent: Intent?): Boolean {
            return intent != null && intent.hasExtra(DrivingActivity::class.java.simpleName)
        }

        fun extractResult(intent: Intent): DrivingActivity {
            return intent.getSerializableExtra(DrivingActivity::class.java.simpleName) as DrivingActivity
        }
    }
}

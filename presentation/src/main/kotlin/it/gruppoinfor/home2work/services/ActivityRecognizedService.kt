package it.gruppoinfor.home2work.services

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import org.jetbrains.anko.intentFor
import timber.log.Timber


class ActivityRecognizedService : IntentService("ActivityRecognizedService") {

    companion object {

        const val EXTRA_ACTIVITY = "activity"
        private const val ACTIVITY_TRESHOLD = 75
        private const val MAX_STILL_STATUS_COUNT = 5
        private var isDriving = false
        private var stillStatusCounter = 0

        fun hasResult(intent: Intent): Boolean {
            return intent.hasExtra(EXTRA_ACTIVITY)
        }

        fun extractResult(intent: Intent): DrivingActivity {
            return intent.getSerializableExtra(EXTRA_ACTIVITY) as DrivingActivity
        }
    }

    override fun onHandleIntent(intent: Intent?) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            handleDetectedActivities(result.probableActivities)
        }

    }

    private fun handleDetectedActivities(probableActivities: List<DetectedActivity>) {
        probableActivities
                .asSequence()
                .filter { it.confidence >= ACTIVITY_TRESHOLD }
                .forEach {
                    when (it.type) {
                        DetectedActivity.IN_VEHICLE -> {
                            startDrivingActivity()
                        }
                        DetectedActivity.ON_FOOT,
                        DetectedActivity.ON_BICYCLE,
                        DetectedActivity.RUNNING -> {
                            stopDrivingActivity()
                        }
                        DetectedActivity.STILL -> {
                            increaseStillStatus()
                        }
                    }
                }
    }

    private fun startDrivingActivity() {
        if (isDriving) {
            return
        }

        startService(intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.STARTED_DRIVING))

        Timber.i("User is driving")

        isDriving = true
        stillStatusCounter = 0
    }

    private fun stopDrivingActivity() {
        if (!isDriving) {
            return
        }

        startService(intentFor<LocationService>(EXTRA_ACTIVITY to DrivingActivity.STOPPED_DRIVING))

        Timber.i("Driving finished")

        isDriving = false
        stillStatusCounter = 0
    }

    private fun increaseStillStatus() {
        if (!isDriving) {
            return
        }

        stillStatusCounter++

        Timber.i("Driving automatically endend in  ${MAX_STILL_STATUS_COUNT - stillStatusCounter}")

        if (stillStatusCounter >= MAX_STILL_STATUS_COUNT) {
            Timber.d("Drving endend for inactivity")
            stopDrivingActivity()
        }
    }

    enum class DrivingActivity {
        STARTED_DRIVING, STOPPED_DRIVING
    }


}

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
        private const val MAX_STILL_STATUS_COUNT = 2
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

        Timber.d("Detected activity: $probableActivities")

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
                            Timber.d("L'utente non è più in auto")
                            stopDrivingActivity()
                        }
                        DetectedActivity.STILL -> {
                            Timber.v("L'utente è fermo")
                            increaseStillStatus()
                        }
                    }


                }

    }

    private fun startDrivingActivity() {

        if (isDriving) {
            return
        }

        startService(intentFor<LiteLocationService>(EXTRA_ACTIVITY to DrivingActivity.STARTED_DRIVING))

        Timber.d("L'utente è sta guidando")

        isDriving = true
        stillStatusCounter = 0

    }

    private fun stopDrivingActivity() {

        if (!isDriving) {
            return
        }

        startService(intentFor<LiteLocationService>(EXTRA_ACTIVITY to DrivingActivity.STOPPED_DRIVING))

        Timber.i("Termine guida")

        isDriving = false
        stillStatusCounter = 0

    }

    private fun increaseStillStatus() {

        if (!isDriving) {
            return
        }

        stillStatusCounter++

        Timber.v("Utente fermo. Guida automaticamente terminata tra ${MAX_STILL_STATUS_COUNT - stillStatusCounter}")

        if (stillStatusCounter >= MAX_STILL_STATUS_COUNT) {
            Timber.v("Guida terminata per inattività")
            stopDrivingActivity()
        }

    }

    enum class DrivingActivity {
        STARTED_DRIVING, STOPPED_DRIVING
    }


}

package it.gruppoinfor.home2work.common.services

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import timber.log.Timber


class ActivityRecognizedService : IntentService("ActivityRecognizedService") {

    override fun onHandleIntent(intent: Intent?) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            handleDetectedActivities(result.probableActivities)
        }

    }

    private fun handleDetectedActivities(probableActivities: List<DetectedActivity>) {


        probableActivities
                // Li prendo in sequenza
                .asSequence()
                // Filtro prendendo solo le attività che superano il treshold impostato
                .filter { it.confidence >= CONFIDENCE_TRESHOLD }
                // E per ognuno effettuo il controllo sul tipo di attività
                .forEach {
                    when (it.type) {
                        DetectedActivity.IN_VEHICLE -> if (!isDriving) startDrivingActivity()
                        DetectedActivity.ON_FOOT -> if (isDriving) stopDrivingActivity()
                        DetectedActivity.ON_BICYCLE -> if (isDriving) stopDrivingActivity()
                        DetectedActivity.STILL -> {

                            if (isDriving) {

                                stillStatusCounter++

                                if (stillStatusCounter >= MAX_STILL_STATUS_COUNT) {
                                    Timber.v("Utente fermo")
                                    stopDrivingActivity()
                                } else
                                    Timber.v("Utente fermo. Guida automaticamente terminata tra ${MAX_STILL_STATUS_COUNT - stillStatusCounter}")
                            }

                        }
                    }
                }

    }

    private fun startDrivingActivity() {

        Timber.i("Inizio guida")

        stillStatusCounter = 0

        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(DrivingActivity::class.java.simpleName, DrivingActivity.STARTED_DRIVING)
        startService(intent)

        isDriving = true

    }

    private fun stopDrivingActivity() {

        Timber.i("Termine guida")

        stillStatusCounter = 0

        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(DrivingActivity::class.java.simpleName, DrivingActivity.STOPPED_DRIVING)
        startService(intent)

        isDriving = false

    }

    enum class DrivingActivity {
        STARTED_DRIVING, STOPPED_DRIVING
    }

    companion object {

        private const val CONFIDENCE_TRESHOLD = 80 // Valore minimo di affidabilita' per i trigger delle attivita'
        private const val MAX_STILL_STATUS_COUNT = 2

        private var isDriving = false
        private var stillStatusCounter = 0

        fun hasResult(intent: Intent): Boolean {
            return intent.hasExtra(DrivingActivity::class.java.simpleName)
        }

        fun extractResult(intent: Intent): DrivingActivity {
            return intent.getSerializableExtra(DrivingActivity::class.java.simpleName) as DrivingActivity
        }
    }
}

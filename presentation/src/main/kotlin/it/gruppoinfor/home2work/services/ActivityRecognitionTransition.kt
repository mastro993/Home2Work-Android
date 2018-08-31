package it.gruppoinfor.home2work.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import timber.log.Timber

class ActivityRecognitionTransition : ActivityRecognitionTransitionAbstract() {

    private val TAG = ActivityRecognitionTransition::class.java.simpleName
    lateinit var mContext: Context
    private lateinit var mPendingIntent: PendingIntent


    override fun startTracking(context: Context) {
        mContext = context
        launchTransitionsTracker()
    }

    override fun stopTracking() {
        ActivityRecognition.getClient(mContext).removeActivityTransitionUpdates(mPendingIntent)
                .addOnSuccessListener {
                    Timber.d("Rilevamento attività arrestato")
                    mPendingIntent.cancel()
                }
                .addOnFailureListener { e ->
                    Timber.e(TAG, "Transitions could not be unregistered: $e")
                }
    }

    private fun launchTransitionsTracker() {
        val transitions = ArrayList<ActivityTransition>()

        transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

        /*transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())*/

        transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

       /* transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())*/


        val request = ActivityTransitionRequest(transitions)
        val activityRecognitionClient = ActivityRecognition.getClient(mContext)

        val intent = Intent(mContext, ActivityRecognitionReceiver::class.java)
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)

        val task = activityRecognitionClient.requestActivityTransitionUpdates(request, mPendingIntent)
        task.addOnSuccessListener {
            Timber.d("ActivityRecognition: %1s", "OnSuccess")
        }

        task.addOnFailureListener {
            Timber.e(TAG, "ActivityRecognition could not be started: $it")
        }
    }
}
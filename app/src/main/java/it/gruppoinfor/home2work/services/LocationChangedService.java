package it.gruppoinfor.home2work.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import it.gruppoinfor.home2work.utils.MyLogger;


public class LocationChangedService extends IntentService {

    private static final String TAG = "ACTIVITY_RECOGNITION";
    private static boolean isDriving = false;
    final int ACTIVITY_TRESHOLD = 80; // Valore minimo di affidabilita' per i trigger delle attivita'
    private static int stillStatusCounter = 0;
    private final static int MAX_STILL_STATUS_COUNT = 2;

    public LocationChangedService() {
        super("ActivityRecognizedService");
    }

    public static boolean hasResult(Intent intent) {
        if (intent != null)
            return intent.hasExtra(DrivingActivity.class.getSimpleName());
        else return false;
    }

    public static DrivingActivity extractResult(Intent intent) {
        return (DrivingActivity) intent.getSerializableExtra(DrivingActivity.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            try {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivities(result.getProbableActivities());
            } catch (Exception e) {
                MyLogger.e(TAG, null, e);
            }
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        for (DetectedActivity activity : probableActivities) {
            if (activity.getConfidence() >= ACTIVITY_TRESHOLD) {
                if (activity.getType() == DetectedActivity.IN_VEHICLE) {
                    if (!isDriving) startDrivingActivity();
                    break;
                } else if (activity.getType() == DetectedActivity.ON_FOOT ||
                        activity.getType() == DetectedActivity.ON_BICYCLE) {
                    if (isDriving) stopDrivingActivity();
                    break;
                } else if (activity.getType() == DetectedActivity.STILL) {
                    if (isDriving) {
                        stillStatusCounter += 1;
                        if (stillStatusCounter >= MAX_STILL_STATUS_COUNT)
                            stopDrivingActivity();
                    }
                    break;
                }
            }
        }

    }

    private void startDrivingActivity() {
        stillStatusCounter = 0;
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(DrivingActivity.class.getSimpleName(), DrivingActivity.STARTED_DRIVING);
        startService(intent);
        MyLogger.d(TAG, "Auto rilevata");
        isDriving = true;
    }

    private void stopDrivingActivity() {
        stillStatusCounter = 0;
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(DrivingActivity.class.getSimpleName(), DrivingActivity.STOPPED_DRIVING);
        startService(intent);
        MyLogger.d(TAG, "Auto non rilevata");
        isDriving = false;
    }

}

package it.gruppoinfor.home2work;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    public static final String TAG = PreferenceManager.class.getSimpleName();

    private static final String PREFS_KEY = "it.fleetup.app.preferences";
    private static boolean inited = false;
    private static SharedPreferences prefs;

    public static void init(Context context) {
        if (!inited) {
            prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            inited = true;
            loadPrefs();
        }
    }

    public static void loadPrefs() {
        if (!inited)
            throw new IllegalStateException("PreferenceManager non initializzato");
        UserPrefs.notificationsEnabled = prefs.getBoolean(PrefsKey.NOTIFICATIONS, true);
        UserPrefs.newsNotificationsEnabled = prefs.getBoolean(PrefsKey.NEWS_NOTIFICATIONS, true);
        UserPrefs.messagesNotificationsEnabled = prefs.getBoolean(PrefsKey.MSG_NOTIFICATIONS, true);
        UserPrefs.matchesNotificationsEnabled = prefs.getBoolean(PrefsKey.MATCHES_NOTIFICATIONS, true);
        UserPrefs.activityTrackingEnabled = prefs.getBoolean(PrefsKey.ACTIVITY_TRACKING, true);
        UserPrefs.syncWithData = prefs.getBoolean(PrefsKey.SYNC_WITH_DATA, true);
    }

    public static void setBool(String key, boolean state) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }

    public static void setLong(String key, long number) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, number);
        editor.apply();
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static class PrefsKey {
        public static String NOTIFICATIONS = "notifications";
        public static String NEWS_NOTIFICATIONS = "news_notifications";
        public static String MSG_NOTIFICATIONS = "message_notifications";
        public static String MATCHES_NOTIFICATIONS = "matches_notifications";
        public static String ACTIVITY_TRACKING = "activity_tracking";
        public static String SYNC_WITH_DATA = "sync_with_data";
    }

}

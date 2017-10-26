package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Federico on 01/03/2017.
 * <p>
 * Preferenze utente
 */

public class UserPrefs {

    // Notifiche
    public static boolean notificationsEnabled;
    public static boolean newsNotificationsEnabled;
    public static boolean messagesNotificationsEnabled;
    public static boolean matchesNotificationsEnabled;

    // Tracking
    public static boolean activityTrackingEnabled;

    // Sync
    public static boolean syncWithData;
    public static String lastSync;

    // METHODS
    private static Manager manager;
    private static boolean inited;
    public static void init(Context context){
        manager = new Manager(context);
        inited = true;
    }

    public static Manager getManager(){
        return manager;
    }

    public static class Manager {

        private final String PREFS_KEY = "it.fleetup.app.preferences";
        private SharedPreferences prefs;

        public Manager(Context context){
            prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            loadPrefs();
        }

        public void loadPrefs() {
            notificationsEnabled = prefs.getBoolean(Keys.NOTIFICATIONS, true);
            newsNotificationsEnabled = prefs.getBoolean(Keys.NEWS_NOTIFICATIONS, true);
            messagesNotificationsEnabled = prefs.getBoolean(Keys.MSG_NOTIFICATIONS, true);
            matchesNotificationsEnabled = prefs.getBoolean(Keys.MATCHES_NOTIFICATIONS, true);
            activityTrackingEnabled = prefs.getBoolean(Keys.ACTIVITY_TRACKING, true);
            syncWithData = prefs.getBoolean(Keys.SYNC_WITH_DATA, false);
            lastSync = prefs.getString(Keys.LAST_SYNC, "Mai");
        }

        public void setBool(String key, boolean state) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, state);
            editor.apply();
        }

        public void setLong(String key, long number) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, number);
            editor.apply();
        }

        public void setString(String key, String value) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public void setFloat(String key, float value) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(key, value);
            editor.apply();
        }

    }

    public static class Keys {
        public static String NOTIFICATIONS = "notifications";
        public static String NEWS_NOTIFICATIONS = "news_notifications";
        public static String MSG_NOTIFICATIONS = "message_notifications";
        public static String MATCHES_NOTIFICATIONS = "matches_notifications";
        public static String ACTIVITY_TRACKING = "activity_tracking";
        public static String SYNC_WITH_DATA = "sync_with_data";
        public static String LAST_SYNC = "last_sync";
    }

    public static boolean isInited() {
        return inited;
    }
}

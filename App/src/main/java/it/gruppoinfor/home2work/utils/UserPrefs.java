package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.Contract;

/**
 * Created by Federico on 01/03/2017.
 * <p>
 * Preferenze utente
 */

public class UserPrefs {

    // Chiavi
    public static String NOTIFICATIONS = "notifications";
    public static String NEWS_NOTIFICATIONS = "news_notifications";
    public static String MSG_NOTIFICATIONS = "message_notifications";
    public static String MATCHES_NOTIFICATIONS = "matches_notifications";
    public static String ACTIVITY_TRACKING = "activity_tracking";
    public static String SYNC_WITH_DATA = "sync_with_data";
    public static String LAST_SYNC = "last_sync";

    public static boolean NotificationsEnabled;
    public static boolean NewsNotificationsEnabled;
    public static boolean MessagesNotificationsEnabled;
    public static boolean MatchesNotificationsEnabled;
    public static boolean TrackingEnabled;
    public static boolean SyncWithData;
    public static String LastSync;

    private static Manager manager;

    public static void init(Context context) {
        manager = new Manager(context);
    }

    @Contract(pure = true)
    public static Manager getManager() {
        return manager;
    }

    public static class Manager {

        private final String PREFS_KEY = "it.fleetup.app.preferences";
        private SharedPreferences prefs;

        Manager(Context context) {
            prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            loadPrefs();
        }

        void loadPrefs() {
            NotificationsEnabled = prefs.getBoolean(NOTIFICATIONS, true);
            NewsNotificationsEnabled = prefs.getBoolean(NEWS_NOTIFICATIONS, true);
            MessagesNotificationsEnabled = prefs.getBoolean(MSG_NOTIFICATIONS, true);
            MatchesNotificationsEnabled = prefs.getBoolean(MATCHES_NOTIFICATIONS, true);
            TrackingEnabled = prefs.getBoolean(ACTIVITY_TRACKING, true);
            SyncWithData = prefs.getBoolean(SYNC_WITH_DATA, false);
            LastSync = prefs.getString(LAST_SYNC, "Mai");
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
}

package it.gruppoinfor.home2work.user

import android.content.Context
import android.content.SharedPreferences

import org.jetbrains.annotations.Contract

object UserPrefs {

    @get:Contract(pure = true)
    var manager: Manager
        private set

    init {
        manager = Manager(context)
    }

    // Chiavi
    var NOTIFICATIONS = "notifications"
    var NEWS_NOTIFICATIONS = "news_notifications"
    var MSG_NOTIFICATIONS = "message_notifications"
    var MATCHES_NOTIFICATIONS = "matches_notifications"
    var ACTIVITY_TRACKING = "activity_tracking"
    var SYNC_WITH_DATA = "sync_with_data"
    var LAST_SYNC = "last_sync"

    var NotificationsEnabled: Boolean = false
    var NewsNotificationsEnabled: Boolean = false
    var MessagesNotificationsEnabled: Boolean = false
    var MatchesNotificationsEnabled: Boolean = false
    var TrackingEnabled: Boolean = false
    var SyncWithData: Boolean = false
    var LastSync: String = ""



    fun init(context: Context) {
        manager = Manager(context)
    }

    class Manager internal constructor(context: Context) {

        private val PREFS_KEY = "it.fleetup.app.preferences"
        private val prefs: SharedPreferences

        init {
            prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            loadPrefs()
        }

        internal fun loadPrefs() {
            NotificationsEnabled = prefs.getBoolean(NOTIFICATIONS, true)
            NewsNotificationsEnabled = prefs.getBoolean(NEWS_NOTIFICATIONS, true)
            MessagesNotificationsEnabled = prefs.getBoolean(MSG_NOTIFICATIONS, true)
            MatchesNotificationsEnabled = prefs.getBoolean(MATCHES_NOTIFICATIONS, true)
            TrackingEnabled = prefs.getBoolean(ACTIVITY_TRACKING, true)
            SyncWithData = prefs.getBoolean(SYNC_WITH_DATA, false)
            LastSync = prefs.getString(LAST_SYNC, "Mai")
        }

        fun setBool(key: String, state: Boolean) {
            val editor = prefs.edit()
            editor.putBoolean(key, state)
            editor.apply()
        }

        fun setLong(key: String, number: Long) {
            val editor = prefs.edit()
            editor.putLong(key, number)
            editor.apply()
        }

        fun setString(key: String, value: String) {
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun setFloat(key: String, value: Float) {
            val editor = prefs.edit()
            editor.putFloat(key, value)
            editor.apply()
        }

    }
}

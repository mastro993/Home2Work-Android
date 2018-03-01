package it.gruppoinfor.home2work.utils


object Const {

    // Tabs
    const val HOME_TAB = 0
    const val RANKS_TAB = 0
    const val MATCHES_TAB = 1
    const val SHARES_TAB = 2
    const val PROFILE_TAB = 3

    // Impostazioni
    const val PREFS_NOTIFICATIONS = "NOTIFICATIONS"
    const val PREFS_NOTIFICATIONS_NEWS = "NOTIFICATIONS_NEWS"
    const val PREFS_NOTIFICATIONS_MSG = "NOTIFICATIONS_MSG"
    const val PREFS_NOTIFICATIONS_MATCHES = "NOTIFICATIONS_MATCHES"
    const val PREFs_ACTIVITY_TRACKING = "ACTIVITY_TRACKING"
    const val PREFS_SYNC_WITH_DATA = "SYNC_WITH_DATA"
    const val PREFS_LAST_SYNC = "LAST_SYNC"
    const val PREFS_FIRST_FIREBASE_TOKEN = "PREFS_FIRST_FIREBASE_TOKEN"
    const val PREFS_FIREBASE_TOKEN = "PREFS_FIREBASE_TOKEN"
    const val PREFS_EMAIL = "signin_email"

    // Codici errore accesso
    const val CODE_AUTH = "CODE_AUTH"
    const val CODE_EXPIRED_TOKEN = 1
    const val CODE_ERROR = 2
    const val CODE_NO_INTERNET = 3
    const val CODE_INVALID_CREDENTIALS = 4
    const val CODE_LOGIN_ERROR = 5
    const val CODE_SERVER_ERROR = 6
    const val CODE_NO_ACCESS_TOKEN = 7

    // Permessi
    const val PERMISSION_FINE_LOCATION = 0
    const val CAMERA_PERMISSION_REQUEST_CODE = 1

    // Codici richiesta
    const val REQ_CODE_AVATAR = 1
    const val REQ_CODE_EXTERNAL_STORAGE = 2
    const val REQ_CAMERA = 3

    // Codici broadcast
    const val SHARE_JOIN_REQUEST = "SHARE_JOIN_REQUEST"
    const val SHARE_COMPLETE_REQUEST = "SHARE_COMPLETE_REQUEST"
    const val SHARE_LEAVE_REQUEST = "SHARE_LEAVE_REQUEST"
    const val NEW_MESSAGE_RECEIVED = "NEW_MESSAGE"
    const val NEW_MATCHES = "NEW_MATCHES"

    // FCM
    const val TYPE = "TYPE"
    const val CHAT_ID = "CHAT_ID"
    const val TEXT = "TEXT"

    // Notifiche
    val MATCHES_CHANNELL = "MATCHES_CHANNEL"
    val MESSAGING_CHANNELL = "MESSAGING_CHANNEL"
    val MESSAGING_NOTIFICATION_ID = 3433

    // Extras
    const val EXTRA_SHARE = "extra_share"
    const val EXTRA_USER = "extra_user"
    const val EXTRA_MATCH = "extra_match"
    const val EXTRA_CHAT = "extra_chat"
    const val EXTRA_NEW_CHAT = "extra_new_chat"

    // API
    const val GOOGLE_API_KEY = " AIzaSyB9U5__DMs2on7xXkYG0-1Ll9vC2YXW1Wo "


}
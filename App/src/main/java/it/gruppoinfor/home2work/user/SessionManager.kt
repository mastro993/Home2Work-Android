package it.gruppoinfor.home2work.user

import android.content.Context
import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncService
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.interfaces.LoginCallback
import it.gruppoinfor.home2workapi.model.User


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze e di ripristinarla all'occorrenza (se non scaduta)
 */

object SessionManager {

    val AUTH_CODE = "auth_code"
    private val PREFS_SESSION = "it.home2work.app.session"
    private val PREFS_TOKEN = "token"
    private val PREFS_EMAIL = "email"

    fun storeSession(context: Context, user: User) {
        val prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(PREFS_EMAIL, user.email)
        editor.putString(PREFS_TOKEN, user.token)
        editor.apply()

        val token = FirebaseInstanceId.getInstance().token

        HomeToWorkClient.getInstance().setFcmToken(token)
    }

    fun loadSession(context: Context, callback: SessionManager.SessionCallback) {
        val prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)

        if (HomeToWorkClient.getUser() != null) {
            callback.onValidSession()
            return
        }

        val email = prefs.getString(PREFS_EMAIL, null)
        val token = prefs.getString(PREFS_TOKEN, null)

        if (email == null && token == null) {
            callback.onInvalidSession(0, null)
            return
        }

        HomeToWorkClient.getInstance().login(email, token, true, object : LoginCallback {
            override fun onLoginSuccess() {
                callback.onValidSession()
            }

            override fun onInvalidCredential() {
                clearSession(context)
                callback.onInvalidSession(1, null)
            }

            override fun onLoginError() {
                callback.onInvalidSession(2, null)
            }

            override fun onError(throwable: Throwable) {
                callback.onInvalidSession(2, throwable)
            }
        })
    }

    fun clearSession(context: Context) {
        context.stopService(Intent(context, LocationService::class.java))
        context.stopService(Intent(context, SyncService::class.java))

        val prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    interface SessionCallback {

        /**
         * Sessione trovata e convalidata
         */
        fun onValidSession()

        /**
         * Sessione non trovata o non valida
         *
         * @param code      0: Nessuna sessione, 1: Token non valido, 2: Errore del server
         * @param throwable Throwable opzionale in caso di errore
         */
        fun onInvalidSession(code: Int, throwable: Throwable?)

    }

}

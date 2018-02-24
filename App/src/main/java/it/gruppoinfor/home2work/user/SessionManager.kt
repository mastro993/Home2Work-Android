package it.gruppoinfor.home2work.user

import android.content.Context
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncService
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.interfaces.LoginCallback
import it.gruppoinfor.home2workapi.model.User
import java.net.UnknownHostException


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze e di ripristinarla all'occorrenza (se non scaduta)
 */

object SessionManager {

    private const val PREFS_SESSION = "it.home2work.app.session"
    private const val PREFS_TOKEN = "SESSION_TOKEN"
    private const val PREFS_EMAIL = "SESSION_EMAIL"

    fun storeSession(ctx: Context, user: User?) {
        if (user == null) return

        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(PREFS_EMAIL, user.email)
        editor.putString(PREFS_TOKEN, user.token)
        editor.apply()

        val token = FirebaseInstanceId.getInstance().token
        HomeToWorkClient.getInstance().setFcmToken(token)
    }

    fun loadSession(ctx: Context, callback: SessionCallback) {
        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)

        val user: User? = HomeToWorkClient.user

        if (user != null) {
            callback.onValidSession(user)
            return
        }

        val email = prefs.getString(PREFS_EMAIL, "")
        val token = prefs.getString(PREFS_TOKEN, "")

        if (email.isEmpty() || token.isEmpty()) {
            callback.onInvalidSession(Const.CODE_NO_CREDENTIALS, null)
            return
        }

        HomeToWorkClient.getInstance().login(email, token, true, object : LoginCallback {
            override fun onLoginSuccess(user: User) {

                // Crashlytics log
                Answers.getInstance().logLogin(LoginEvent()
                        .putMethod("Token")
                        .putSuccess(true))

                Crashlytics.setUserIdentifier(user.id.toString())
                Crashlytics.setUserEmail(user.email)
                Crashlytics.setUserName(user.toString())

                callback.onValidSession(user)

            }

            override fun onInvalidCredential() {
                clearSession(ctx)
                callback.onInvalidSession(Const.CODE_INVALID_CREDENTIALS, null)
            }

            override fun onError(throwable: Throwable?) {

                // Crashlytics log
                Answers.getInstance().logLogin(LoginEvent()
                        .putMethod("Token")
                        .putSuccess(false))

                if (throwable is UnknownHostException) {
                    callback.onInvalidSession(Const.CODE_SERVER_ERROR, throwable)
                } else {
                    callback.onInvalidSession(Const.CODE_LOGIN_ERROR, null)
                }

            }
        })
    }

    fun clearSession(ctx: Context) {
        ctx.stopService(Intent(ctx, LocationService::class.java))
        ctx.stopService(Intent(ctx, SyncService::class.java))

        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    interface SessionCallback {
        fun onValidSession(user: User)
        fun onInvalidSession(code: Int, throwable: Throwable?)
    }

}

package it.gruppoinfor.home2work.auth

import android.content.Context
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import it.gruppoinfor.home2work.tracking.LocationService
import it.gruppoinfor.home2work.tracking.SyncJobService
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.auth.AuthCallback
import it.gruppoinfor.home2workapi.auth.AuthUser
import it.gruppoinfor.home2workapi.user.User
import java.net.UnknownHostException


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze e di ripristinarla all'occorrenza (se non scaduta)
 */

object SessionManager {

    private const val PREFS_SESSION = "it.home2work.app.session"
    private const val PREFS_TOKEN = "ACCESS_TOKEN"

    fun storeSession(ctx: Context, user: AuthUser?) {
        if (user == null) return

        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(PREFS_TOKEN, user.accessToken)
        editor.apply()

    }

    fun loadSession(context: Context, callback: SessionCallback) {
        val prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)

        val user: User? = HomeToWorkClient.user

        if (user != null) {
            callback.onValidSession()
            return
        }

        val token = prefs.getString(PREFS_TOKEN, "")

        if (token.isEmpty()) {
            callback.onInvalidSession(SignInActivity.CODE_NO_ACCESS_TOKEN, null)
            return
        }

        HomeToWorkClient.login(token, object : AuthCallback {
            override fun onSuccess() {

                // Crashlytics log
                Answers.getInstance().logLogin(LoginEvent()
                        .putMethod("Token")
                        .putSuccess(true))

                Crashlytics.setUserIdentifier(HomeToWorkClient.user?.id.toString())
                Crashlytics.setUserEmail(HomeToWorkClient.user?.email)
                Crashlytics.setUserName(HomeToWorkClient.user.toString())

                callback.onValidSession()

            }

            override fun onInvalidCredential() {
                clearSession(context)
                callback.onInvalidSession(SignInActivity.CODE_INVALID_CREDENTIALS, null)
            }

            override fun onError(throwable: Throwable?) {
                clearSession(context)
                // Crashlytics log
                Answers.getInstance().logLogin(LoginEvent()
                        .putMethod("Token")
                        .putSuccess(false))

                if (throwable is UnknownHostException) {
                    callback.onInvalidSession(SignInActivity.CODE_SERVER_ERROR, throwable)
                } else {
                    callback.onInvalidSession(SignInActivity.CODE_LOGIN_ERROR, null)
                }

            }
        })
    }

    fun clearSession(context: Context) {
        context.stopService(Intent(context, LocationService::class.java))
        SyncJobService.remove(context)

        val prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    interface SessionCallback {
        fun onValidSession()
        fun onInvalidSession(code: Int, throwable: Throwable?)
    }

}

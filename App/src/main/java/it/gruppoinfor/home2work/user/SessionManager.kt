package it.gruppoinfor.home2work.user

import android.content.Context
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.services.FirebaseTokenService
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncService
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.callback.LoginCallback
import it.gruppoinfor.home2workapi.model.ClientUser
import it.gruppoinfor.home2workapi.model.User
import java.net.UnknownHostException


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze e di ripristinarla all'occorrenza (se non scaduta)
 */

object SessionManager {

    private const val PREFS_SESSION = "it.home2work.app.session"
    private const val PREFS_TOKEN = "ACCESS_TOKEN"

    fun storeSession(ctx: Context, user: ClientUser?) {
        if (user == null) return

        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(PREFS_TOKEN, user.accessToken)
        editor.apply()

    }

    fun loadSession(ctx: Context, callback: SessionCallback) {
        val prefs = ctx.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE)

        val user: User? = HomeToWorkClient.user

        if (user != null) {
            callback.onValidSession()
            return
        }

        val token = prefs.getString(PREFS_TOKEN, "")

        if (token.isEmpty()) {
            callback.onInvalidSession(Const.CODE_NO_ACCESS_TOKEN, null)
            return
        }

        HomeToWorkClient.login(token, object : LoginCallback {
            override fun onLoginSuccess() {

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
        fun onValidSession()
        fun onInvalidSession(code: Int, throwable: Throwable?)
    }

}

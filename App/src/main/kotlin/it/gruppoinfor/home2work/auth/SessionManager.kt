package it.gruppoinfor.home2work.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException
import it.gruppoinfor.home2work.tracking.LocationService
import it.gruppoinfor.home2work.tracking.SyncJobService
import it.gruppoinfor.home2work.user.User


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

    @SuppressLint("CheckResult")
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

        HomeToWorkClient.getAuthService().login(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { null }
                .subscribe({

                    HomeToWorkClient.user = it

                    // Crashlytics log
                    Answers.getInstance().logLogin(LoginEvent()
                            .putMethod("Token")
                            .putSuccess(true))

                    Crashlytics.setUserIdentifier(HomeToWorkClient.user?.id.toString())
                    Crashlytics.setUserEmail(HomeToWorkClient.user?.email)
                    Crashlytics.setUserName(HomeToWorkClient.user.toString())

                    callback.onValidSession()

                }, {

                    clearSession(context)
                    Answers.getInstance().logLogin(LoginEvent()
                            .putMethod("Token")
                            .putSuccess(false))

                    when ((it as RetrofitException).kind) {
                        RetrofitException.Kind.NETWORK -> {
                            callback.onInvalidSession(SignInActivity.CODE_LOGIN_ERROR, null)
                        }
                        RetrofitException.Kind.HTTP -> {
                            callback.onInvalidSession(SignInActivity.CODE_INVALID_CREDENTIALS, null)
                        }
                        RetrofitException.Kind.UNEXPECTED -> {
                            callback.onInvalidSession(SignInActivity.CODE_SERVER_ERROR, it)
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

package it.gruppoinfor.home2work.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User

class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(this::class.java.name.toUpperCase(), "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession(user: User) {
                HomeToWorkClient.getInstance().setFcmToken(refreshedToken)
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        })
    }


}

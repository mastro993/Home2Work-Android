package it.gruppoinfor.home2work.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User

class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token

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

package it.gruppoinfor.home2work.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient

class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token

        SessionManager.loadSession(this, object : SessionManager.SessionCallback {
            override fun onValidSession() {
                HomeToWorkClient.updateFcmToken(refreshedToken)
            }

            override fun onInvalidSession(code: Int, throwable: Throwable?) {
                throwable?.printStackTrace()
            }
        })

    }


}

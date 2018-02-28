package it.gruppoinfor.home2work.services

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient


class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token

        HomeToWorkClient.updateFcmToken(refreshedToken, OnSuccessListener {

            Prefs.putString(Const.PREFS_FIREBASE_TOKEN, refreshedToken)

        }, OnFailureListener {
            it.printStackTrace()
        })


    }


}

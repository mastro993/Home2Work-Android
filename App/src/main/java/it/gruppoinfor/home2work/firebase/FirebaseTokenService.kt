package it.gruppoinfor.home2work.firebase

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2workapi.HomeToWorkClient
import timber.log.Timber


class FirebaseTokenService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token

        Timber.i("FirebaseToken aggiornato: $refreshedToken")

        HomeToWorkClient.updateFcmToken(refreshedToken, OnSuccessListener {

            Timber.i("FirebaseToken sincronizzato con il server")

            Prefs.putString(FirebaseTokenService.PREFS_FIREBASE_TOKEN, refreshedToken)

        }, OnFailureListener {

            Timber.e(it, "Errore sincronizzazione FirebaseToken!")

        })


    }

    companion object {
        const val PREFS_FIREBASE_TOKEN = "PREFS_FIREBASE_TOKEN"
    }


}

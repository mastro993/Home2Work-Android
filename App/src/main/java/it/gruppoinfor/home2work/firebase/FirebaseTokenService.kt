package it.gruppoinfor.home2work.firebase

import android.annotation.SuppressLint
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import timber.log.Timber


class FirebaseTokenService : FirebaseInstanceIdService() {

    @SuppressLint("CheckResult")
    override fun onTokenRefresh() {

        val refreshedToken = FirebaseInstanceId.getInstance().token

        Timber.i("FirebaseToken aggiornato: $refreshedToken")


        sync(token = refreshedToken)


    }

    companion object {
        const val PREFS_FIREBASE_TOKEN = "PREFS_FIREBASE_TOKEN"

        fun sync(token: String?) {
            HomeToWorkClient.getUserService().updateFCMToken(token!!)
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn {
                        null
                    }
                    .subscribe({

                        Timber.i("FirebaseToken sincronizzato con il server")
                        Prefs.putString(FirebaseTokenService.PREFS_FIREBASE_TOKEN, token)

                    }, {

                        Timber.e(it, "Errore sincronizzazione FirebaseToken!")

                    })
        }

    }


}

package it.gruppoinfor.home2work.firebase

import android.annotation.SuppressLint
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import timber.log.Timber
import javax.inject.Inject


class FirebaseTokenService : FirebaseInstanceIdService() {

    @Inject
    lateinit var storeUserFCMToken: StoreUserFCMToken

    @SuppressLint("CheckResult")
    override fun onTokenRefresh() {
        DipendencyInjector.createFirebaseComponent().inject(this)

        val refreshedToken = FirebaseInstanceId.getInstance().token

        Timber.i("FirebaseToken aggiornato: $refreshedToken")

        sync(token = refreshedToken)

    }

    fun sync(token: String?) {
        token?.let {

            storeUserFCMToken.store(it)
                    .subscribe({}, {})

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseFirebaseComponent()
    }


}

package it.gruppoinfor.home2work.services

import android.annotation.SuppressLint
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import timber.log.Timber
import javax.inject.Inject


class FirebaseTokenService : FirebaseInstanceIdService() {

    @Inject
    lateinit var storeUserFCMToken: StoreUserFCMToken
    @Inject
    lateinit var localUserData: LocalUserData

    @SuppressLint("CheckResult")
    override fun onTokenRefresh() {
        DipendencyInjector.createFirebaseComponent().inject(this)

        val refreshedToken = FirebaseInstanceId.getInstance().token

        refreshedToken?.let {
            val savedToken = localUserData.firebaseToken
            if (it != savedToken) {
                syncToken(it)
            }
        }
    }

    fun syncToken(token: String?) {
        token?.let {
            storeUserFCMToken.store(it)
                    .subscribe({
                        localUserData.firebaseToken = token
                    }, {
                        Timber.e(it, "Impossibile aggiornare il Firebase Token")
                    })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseFirebaseComponent()
    }


}

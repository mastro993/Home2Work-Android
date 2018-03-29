package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.FCMTokenService
import it.gruppoinfor.home2work.domain.interfaces.FirebaseTokenRepository

/**
 * Created by feder on 20/03/2018.
 */
class FirebaseTokenRepositoryImpl : FirebaseTokenRepository {

    private val fcmTokenService = APIService.get<FCMTokenService>()

    override fun syncUserFCMToken(token: String): Observable<Boolean> {
        return fcmTokenService.updateFCMToken(token)
    }
}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Location
import it.gruppoinfor.home2work.domain.interfaces.FirebaseTokenRepository
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class StoreUserFCMToken(
        transformer: Transformer<Boolean>,
        private val firebaseTokenRepository: FirebaseTokenRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_FCM_TOKEN = "param:FCMToken"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val token = data?.get(PARAM_FCM_TOKEN)


        token?.let {
            return firebaseTokenRepository.syncUserFCMToken(token as String)
        } ?: return Observable.error(IllegalArgumentException("FCMToken must be provided."))


    }

}
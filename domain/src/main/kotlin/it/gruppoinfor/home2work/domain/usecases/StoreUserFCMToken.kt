package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.UserRepository

class StoreUserFCMToken(
        transformer: Transformer<Boolean>,
        private val userRepository: UserRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_FCM_TOKEN = "param:FCMToken"
    }

    fun store(token: String): Observable<Boolean> {
        val data = HashMap<String, String>()
        data[PARAM_FCM_TOKEN] = token
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val token = data?.get(PARAM_FCM_TOKEN)


        token?.let {
            return userRepository.syncUserFCMToken(token as String)
        } ?: return Observable.error(IllegalArgumentException("FCMToken must be provided."))


    }

}
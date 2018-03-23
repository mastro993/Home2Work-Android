package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class UserTokenLogin(
        transformer: Transformer<UserEntity>,
        private val userRepository: UserRepository
) : UseCase<UserEntity>(transformer) {

    companion object {
        private const val PARAM_TOKEN = "param:token"
    }

    fun login(token: String): Observable<UserEntity> {
        val data = HashMap<String, String>()
        data[PARAM_TOKEN] = token
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<UserEntity> {
        val token = data?.get(PARAM_TOKEN)

        token?.let {
            return userRepository.login(token as String)
        } ?: return Observable.error(IllegalArgumentException("token must be provided."))

    }

}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ClientUser
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class UserTokenLogin(
        transformer: Transformer<ClientUser>,
        private val userRepository: UserRepository
) : UseCase<ClientUser>(transformer) {

    companion object {
        private const val PARAM_TOKEN = "param:token"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ClientUser> {
        val token = data?.get(PARAM_TOKEN)

        token?.let {
            return userRepository.login(token as String)
        } ?: return Observable.error(IllegalArgumentException("token must be provided."))

    }

}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.User
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUser(
        transformer: Transformer<User>,
        private val userRepository: UserRepository
) : UseCase<User>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<User> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return userRepository.getUser(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))

    }

}
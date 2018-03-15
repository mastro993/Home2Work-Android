package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserProfile
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUserProfile(
        transformer: Transformer<UserProfile>,
        private val userRepository: UserRepository
) : UseCase<UserProfile>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<UserProfile> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return userRepository.getUserProfile(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))

    }

}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.UserProfileEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUserProfile(
        transformer: Transformer<UserProfileEntity>,
        private val userRepository: UserRepository
) : UseCase<UserProfileEntity>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    fun getById(userId: Long): Observable<UserProfileEntity> {
        val data = HashMap<String, Long>()
        data[PARAM_USER_ID] = userId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<UserProfileEntity> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return userRepository.getUserProfile(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))

    }

}
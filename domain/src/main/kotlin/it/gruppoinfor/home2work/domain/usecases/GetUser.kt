package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUser(
        transformer: Transformer<UserEntity>,
        private val userRepository: UserRepository
) : UseCase<UserEntity>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    fun getById(userId: Long): Observable<UserEntity> {
        val data = HashMap<String, Long>()
        data[PARAM_USER_ID] = userId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<UserEntity> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return userRepository.getUser(userId as Long)
        } ?: return userRepository.getUser()

    }

}
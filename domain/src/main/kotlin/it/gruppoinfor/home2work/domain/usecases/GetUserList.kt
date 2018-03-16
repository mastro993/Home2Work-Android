package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUserList(
        transformer: Transformer<List<UserEntity>>,
        private val userRepository: UserRepository
) : UseCase<List<UserEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<UserEntity>> {
        return userRepository.getUserList()
    }
}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.User
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetUserList(
        transformer: Transformer<List<User>>,
        private val userRepository: UserRepository
) : UseCase<List<User>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<User>> {
        return userRepository.getUserList()
    }
}
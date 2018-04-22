package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class HideUserStatus(
        transformer: Transformer<Boolean>,
        private val userRepository: UserRepository
) : UseCase<Boolean>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        return userRepository.hideStatus()
    }
}
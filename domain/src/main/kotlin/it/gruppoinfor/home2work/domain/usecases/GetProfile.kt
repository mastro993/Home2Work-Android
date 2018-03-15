package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserProfile
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetProfile(
        transformer: Transformer<UserProfile>,
        private val userRepository: UserRepository
) : UseCase<UserProfile>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<UserProfile> {
        return userRepository.getProfile()
    }

}
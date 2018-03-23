package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetProfile(
        transformer: Transformer<ProfileEntity>,
        private val userRepository: UserRepository
) : UseCase<ProfileEntity>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<ProfileEntity> {
        return userRepository.getProfile()
    }

}
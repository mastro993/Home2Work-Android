package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.entities.UserProfileEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class GetProfile(
        transformer: Transformer<UserProfileEntity>,
        private val userRepository: UserRepository
) : UseCase<UserProfileEntity>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<UserProfileEntity> {
        return userRepository.getProfile()
    }

}
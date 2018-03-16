package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class FinishCurrentShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        return shareRepository.finishShare()
    }
}
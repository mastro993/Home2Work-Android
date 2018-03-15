package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CancelCurrentShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        return shareRepository.cancelShare()
    }
}
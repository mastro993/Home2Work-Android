package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class GetActiveShare(
        transformer: Transformer<Optional<ShareEntity>>,
        private val shareRepository: ShareRepository
) : UseCase<Optional<ShareEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<Optional<ShareEntity>> {
        return shareRepository.getActiveShare()
    }
}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class GetActiveShare(
        transformer: Transformer<ShareEntity>,
        private val shareRepository: ShareRepository
) : UseCase<ShareEntity>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<ShareEntity> {
        return shareRepository.getActiveShare()
    }
}
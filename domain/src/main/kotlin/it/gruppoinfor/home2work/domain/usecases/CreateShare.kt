package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Share
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CreateShare(
        transformer: Transformer<Share>,
        private val shareRepository: ShareRepository
) : UseCase<Share>(transformer) {


    override fun createObservable(data: Map<String, Any>?): Observable<Share> {
        return shareRepository.createShare()
    }
}
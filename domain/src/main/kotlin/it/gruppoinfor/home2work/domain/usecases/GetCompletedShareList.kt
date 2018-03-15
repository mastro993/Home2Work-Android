package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Share
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository


class GetCompletedShareList(
        transformer: Transformer<List<Share>>,
        private val shareRepository: ShareRepository
) : UseCase<List<Share>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<Share>> {
        return shareRepository.getCompletedShareList()
    }
}
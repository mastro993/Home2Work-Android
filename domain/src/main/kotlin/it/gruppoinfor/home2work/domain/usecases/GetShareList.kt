package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository


class GetShareList(
        transformer: Transformer<List<ShareEntity>>,
        private val shareRepository: ShareRepository
) : UseCase<List<ShareEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<ShareEntity>> {
        return shareRepository.getShareList()
    }
}
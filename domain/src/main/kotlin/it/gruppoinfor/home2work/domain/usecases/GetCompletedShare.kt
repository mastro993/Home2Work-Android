package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Share
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class GetCompletedShare(
        transformer: Transformer<Share>,
        private val shareRepository: ShareRepository
) : UseCase<Share>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Share> {
        val shareId = data?.get(PARAM_SHARE_ID)
        shareId?.let {
            return shareRepository.getCompletedShare(shareId as Long)
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class GetShare(
        transformer: Transformer<Optional<ShareEntity>>,
        private val shareRepository: ShareRepository
) : UseCase<Optional<ShareEntity>>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
    }

    fun getById(shareId: Long): Observable<Optional<ShareEntity>>{
        val data = HashMap<String, Long>()
        data[PARAM_SHARE_ID] = shareId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Optional<ShareEntity>> {
        val shareId = data?.get(PARAM_SHARE_ID)
        shareId?.let {
            return shareRepository.getShare(shareId as Long)
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
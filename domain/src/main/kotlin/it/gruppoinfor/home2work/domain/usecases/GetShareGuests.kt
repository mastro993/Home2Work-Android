package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class GetShareGuests(
        transformer: Transformer<List<GuestEntity>>,
        private val shareRepository: ShareRepository
) : UseCase<List<GuestEntity>>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
    }

    fun getById(shareId: Long): Observable<List<GuestEntity>>{
        val data = HashMap<String, Long>()
        data[PARAM_SHARE_ID] = shareId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<GuestEntity>> {
        val shareId = data?.get(PARAM_SHARE_ID)
        shareId?.let {
            return shareRepository.getShareGuests(shareId as Long)
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
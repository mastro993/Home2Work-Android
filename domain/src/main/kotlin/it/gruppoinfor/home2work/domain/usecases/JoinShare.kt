package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLng
import it.gruppoinfor.home2work.domain.entities.Share
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class JoinShare(
        transformer: Transformer<Share>,
        private val shareRepository: ShareRepository
) : UseCase<Share>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
        private const val PARAM_JOIN_LATLNG = "param:joinLatLng"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Share> {
        val shareId = data?.get(PARAM_SHARE_ID)
        val joinLatLng = data?.get(PARAM_JOIN_LATLNG)

        shareId?.let {
            joinLatLng?.let {
                return shareRepository.joinShare(shareId as Long, joinLatLng as LatLng)
            } ?: return Observable.error(IllegalArgumentException("joinLatLng must be provided."))
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
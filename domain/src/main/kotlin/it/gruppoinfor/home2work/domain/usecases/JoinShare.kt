package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class JoinShare(
        transformer: Transformer<ShareEntity>,
        private val shareRepository: ShareRepository
) : UseCase<ShareEntity>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
        private const val PARAM_JOIN_LATLNG = "param:joinLatLng"
    }

    fun join(shareId: Long, joinLatLng: LatLngEntity): Observable<ShareEntity> {
        val data = HashMap<String, Any>()
        data[PARAM_SHARE_ID] = shareId
        data[PARAM_JOIN_LATLNG] = joinLatLng
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ShareEntity> {
        val shareId = data?.get(PARAM_SHARE_ID)
        val joinLatLng = data?.get(PARAM_JOIN_LATLNG)

        shareId?.let {
            joinLatLng?.let {
                return shareRepository.joinShare(shareId as Long, joinLatLng as LatLngEntity)
            } ?: return Observable.error(IllegalArgumentException("joinLatLng must be provided."))
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
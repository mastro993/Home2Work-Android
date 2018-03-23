package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class JoinShare(
        transformer: Transformer<ShareEntity>,
        private val shareRepository: ShareRepository
) : UseCase<ShareEntity>(transformer) {

    companion object {
        private const val PARAM_SHARE_ID = "param:shareId"
        private const val PARAM_JOIN_LAT = "param:joinLat"
        private const val PARAM_JOIN_LNG = "param:joinLng"
    }

    fun join(shareId: Long, joinLat: Double, joinLng: Double): Observable<ShareEntity> {
        val data = HashMap<String, Any>()
        data[PARAM_SHARE_ID] = shareId
        data[PARAM_JOIN_LAT] = joinLat
        data[PARAM_JOIN_LNG] = joinLng
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ShareEntity> {
        val shareId = data?.get(PARAM_SHARE_ID)
        val joinLat = data?.get(PARAM_JOIN_LAT)
        val joinLng = data?.get(PARAM_JOIN_LNG)

        shareId?.let { id ->
            joinLat?.let { lat ->
                joinLng?.let { lng ->
                    return shareRepository.joinShare(id as Long, lat as Double, lng as Double)
                } ?: return Observable.error(IllegalArgumentException("joinLng must be provided."))
            } ?: return Observable.error(IllegalArgumentException("joinLat must be provided."))
        } ?: return Observable.error(IllegalArgumentException("shareId must be provided."))
    }
}
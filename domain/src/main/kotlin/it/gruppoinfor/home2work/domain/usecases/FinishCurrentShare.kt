package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class FinishCurrentShare(
        transformer: Transformer<ShareEntity>,
        private val shareRepository: ShareRepository
) : UseCase<ShareEntity>(transformer) {

    companion object {
        private const val PARAM_FINISH_LAT = "param:finishLat"
        private const val PARAM_FINISH_LNG = "param:finishLng"
    }

    fun finishFrom(latitude: Double, longitude: Double): Observable<ShareEntity> {
        val data = HashMap<String, Double>()
        data[PARAM_FINISH_LAT] = latitude
        data[PARAM_FINISH_LNG] = longitude
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ShareEntity> {
        val completeLat = data?.get(PARAM_FINISH_LAT)
        val completeLng = data?.get(PARAM_FINISH_LNG)


        completeLat?.let { lat ->
            completeLng?.let { lng ->
                return shareRepository.finishShare(lat as Double, lng as Double)
            } ?: return Observable.error(IllegalArgumentException("finishLat must be provided."))
        } ?: return Observable.error(IllegalArgumentException("finishLng must be provided."))

    }
}
package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CreateShare(
        transformer: Transformer<ShareEntity>,
        private val shareRepository: ShareRepository
) : UseCase<ShareEntity>(transformer) {

    companion object {
        private const val PARAM_START_LAT = "param:startLat"
        private const val PARAM_START_LNG = "param:startLng"
    }

    fun startFrom(latitude: Double, longitude: Double): Observable<ShareEntity> {
        val data = HashMap<String, Double>()
        data[PARAM_START_LAT] = latitude
        data[PARAM_START_LNG] = longitude
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ShareEntity> {
        val completeLat = data?.get(PARAM_START_LAT)
        val completeLng = data?.get(PARAM_START_LNG)


        completeLat?.let { lat ->
            completeLng?.let { lng ->
                return shareRepository.createShare(lat as Double, lng as Double)
            } ?: return Observable.error(IllegalArgumentException("startLat must be provided."))
        } ?: return Observable.error(IllegalArgumentException("startLng must be provided."))

    }
}
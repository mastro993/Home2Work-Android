package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CompleteCurrentShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_COMPLETE_LAT = "param:completeLat"
        private const val PARAM_COMPLETE_LNG = "param:completeLng"
    }

    fun completeFrom(latitude: Double, longitude: Double): Observable<Boolean> {
        val data = HashMap<String, Double>()
        data[PARAM_COMPLETE_LAT] = latitude
        data[PARAM_COMPLETE_LNG] = longitude
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val completeLat = data?.get(PARAM_COMPLETE_LAT)
        val completeLng = data?.get(PARAM_COMPLETE_LNG)


        completeLat?.let { lat ->
            completeLng?.let { lng ->
                return shareRepository.completeShare(lat as Double, lng as Double)
            } ?: return Observable.error(IllegalArgumentException("completeLat must be provided."))
        } ?: return Observable.error(IllegalArgumentException("completeLng must be provided."))

    }
}
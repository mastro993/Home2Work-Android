package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CompleteCurrentShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_COMPLETE_LATLNG = "param:completeLatLng"
    }

    fun completeFrom(latLngEntity: LatLngEntity): Observable<Boolean>{
        val data = HashMap<String, LatLngEntity>()
        data[PARAM_COMPLETE_LATLNG] = latLngEntity
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val completeLatLng = data?.get(PARAM_COMPLETE_LATLNG)

        completeLatLng?.let {
            return shareRepository.completeShare(completeLatLng as LatLngEntity)
        } ?: return Observable.error(IllegalArgumentException("completeLatLng must be provided."))
    }
}
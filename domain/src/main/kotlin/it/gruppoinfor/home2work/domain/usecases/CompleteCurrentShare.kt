package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLng
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class CompleteCurrentShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_COMPLETE_LATLNG = "param:completeLatLng"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val completeLatLng = data?.get(PARAM_COMPLETE_LATLNG)

        completeLatLng?.let {
            return shareRepository.completeShare(completeLatLng as LatLng)
        } ?: return Observable.error(IllegalArgumentException("completeLatLng must be provided."))
    }
}
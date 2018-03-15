package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Location
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class UploadUserLocations(
        transformer: Transformer<Boolean>,
        private val locationRepository: LocationRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_LOCATIONS = "param:locations"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val locations = data?.get(PARAM_LOCATIONS)


        locations?.let {
            return locationRepository.syncUserLocations(locations as List<Location>)
        } ?: return Observable.error(IllegalArgumentException("location list must be provided."))


    }

}
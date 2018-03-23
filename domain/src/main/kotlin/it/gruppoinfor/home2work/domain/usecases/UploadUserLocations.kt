package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class UploadUserLocations(
        transformer: Transformer<Boolean>,
        private val locationRepository: LocationRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_LOCATIONS = "param:locations"
    }

    fun upload(locations: List<UserLocationEntity>): Observable<Boolean> {
        val data = HashMap<String, List<UserLocationEntity>>()
        data[PARAM_LOCATIONS] = locations
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val locations = data?.get(PARAM_LOCATIONS)


        locations?.let {
            return locationRepository.syncUserLocations(locations as List<UserLocationEntity>)
        } ?: return Observable.error(IllegalArgumentException("latLng list must be provided."))


    }

}
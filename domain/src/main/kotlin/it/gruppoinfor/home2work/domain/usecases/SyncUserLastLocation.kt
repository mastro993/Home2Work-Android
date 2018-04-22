package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class SyncUserLastLocation(
        transformer: Transformer<Boolean>,
        private val locationRepository: LocationRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_LOCATION = "param:location"
    }

    fun upload(location: UserLocationEntity): Observable<Boolean> {
        val data = HashMap<String, UserLocationEntity>()
        data[PARAM_LOCATION] = location
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val location = data?.get(PARAM_LOCATION)

        location?.let {
            return locationRepository.syncUserLastLocation(location as UserLocationEntity)

        } ?: return Observable.error(IllegalArgumentException("location must be provided."))
    }
}
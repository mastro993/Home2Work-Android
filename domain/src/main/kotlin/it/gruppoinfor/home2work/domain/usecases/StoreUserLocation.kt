package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class StoreUserLocation(
        transformer: Transformer<Long>,
        private val locationRepository: LocationRepository
) : UseCase<Long>(transformer) {

    companion object {
        private const val PARAM_USER_LOCAION = "param:userLocation"
    }

    fun save(userLocationEntity: UserLocationEntity): Observable<Long> {
        val data = HashMap<String, UserLocationEntity>()
        data[PARAM_USER_LOCAION] = userLocationEntity
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Long> {
        val userLocation = data?.get(PARAM_USER_LOCAION)
        userLocation?.let {
            return locationRepository.saveLocation(userLocation as UserLocationEntity)
        } ?: return Observable.error(IllegalArgumentException("userLocation must be provided."))


    }

}
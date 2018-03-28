package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class GetUserLocations(
        transformer: Transformer<List<UserLocationEntity>>,
        private val locationRepository: LocationRepository
) : UseCase<List<UserLocationEntity>>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    fun byId(userId: Long): Observable<List<UserLocationEntity>> {
        val data = HashMap<String, Long>()
        data[PARAM_USER_ID] = userId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<UserLocationEntity>> {
        val userId = data?.get(PARAM_USER_ID)


        userId?.let {
            return locationRepository.getUserLocations(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("latLng list must be provided."))


    }

}
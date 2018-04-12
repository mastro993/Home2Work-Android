package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class DeleteUserLocations(
        transformer: Transformer<Boolean>,
        private val locationRepository: LocationRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    fun byId(userId: Long): Observable<Boolean> {
        val data = HashMap<String, Long>()
        data[PARAM_USER_ID] = userId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val userId = data?.get(PARAM_USER_ID)


        userId?.let {
            return locationRepository.deleteUserLocations(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))


    }

}
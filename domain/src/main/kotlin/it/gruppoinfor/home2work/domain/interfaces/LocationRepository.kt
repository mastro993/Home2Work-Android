package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity

interface LocationRepository {
    fun syncUserLocations(userLocations: List<UserLocationEntity>): Observable<Boolean>
}
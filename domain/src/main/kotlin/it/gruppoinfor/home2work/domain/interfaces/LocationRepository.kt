package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity

interface LocationRepository {
    fun getUserLocations(userId: Long): Observable<List<UserLocationEntity>>
    fun saveLocation(userLocation: UserLocationEntity): Observable<Long>
    fun syncUserLocations(userLocations: List<UserLocationEntity>): Observable<Boolean>
}
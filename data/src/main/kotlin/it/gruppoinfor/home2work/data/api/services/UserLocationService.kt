package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import retrofit2.http.Body
import retrofit2.http.POST

interface UserLocationService {
    @POST("user/latLng")
    fun uploadLocations(
            @Body userLocations: List<UserLocationEntity>
    ): Observable<List<UserLocationEntity>>
}
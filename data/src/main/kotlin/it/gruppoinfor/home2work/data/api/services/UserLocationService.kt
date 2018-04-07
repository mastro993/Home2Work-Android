package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.UserLocationData
import retrofit2.http.Body
import retrofit2.http.POST

interface UserLocationService {
    @POST("user/location")
    fun uploadLocations(
            @Body userLocations: List<UserLocationData>
    ): Observable<Boolean>
}
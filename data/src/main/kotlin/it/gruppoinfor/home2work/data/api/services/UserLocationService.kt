package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Location
import retrofit2.http.Body
import retrofit2.http.POST

interface UserLocationService {
    @POST("user/location")
    fun uploadLocations(
            @Body locations: List<Location>
    ): Observable<List<Location>>
}
package it.gruppoinfor.home2workapi.match

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


interface MatchService {


    @GET("match/{id}")
    fun getMatchById(
            @Path("id") id: Long?
    ): Observable<Match>

    @PUT("match")
    fun editMatch(
            @Body match: Match
    ): Observable<Match>

}
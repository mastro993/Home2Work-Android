package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.model.Match
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


internal interface MatchService {



    @GET("match/{id}")
    fun getMatchById(
            @Path("id") id: Long?
    ): Observable<Response<Match>>

    @PUT("match")
    fun editMatch(
            @Body match: Match
    ): Observable<Response<Match>>

}
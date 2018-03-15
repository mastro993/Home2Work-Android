package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Match
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

    @GET("user/match")
    fun getMatchList(): Observable<ArrayList<Match>>
}
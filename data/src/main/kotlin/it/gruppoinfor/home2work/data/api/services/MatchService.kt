package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.MatchData
import it.gruppoinfor.home2work.domain.entities.Optional
import retrofit2.http.*


interface MatchService {

    @GET("match/{id}")
    fun getMatchById(
            @Path("id") id: Long?
    ): Observable<Optional<MatchData>>

    @PUT("match")
    fun editMatch(
            @Body match: MatchData
    ): Observable<Boolean>

    @GET("match/list")
    fun getMatchList(
            @Query("limit") limit: Int?,
            @Query("page") page: Int?
    ): Observable<ArrayList<MatchData>>
}
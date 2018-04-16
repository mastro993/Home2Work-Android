package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.LeaderboardData
import it.gruppoinfor.home2work.data.entities.UserRankingData
import retrofit2.http.GET
import retrofit2.http.Query


interface LeaderboardsService {


    @GET("user/leaderboard")
    fun getUserLeaderboard(
            @Query("type") type: LeaderboardData.Type?,
            @Query("range") range: LeaderboardData.Range?,
            @Query("timespan") timeSpan: LeaderboardData.TimeSpan?,
            @Query("companyId") companyId: Long?,
            @Query("page") page: Int?,
            @Query("limit") limit: Int?
    ): Observable<List<UserRankingData>>

}
package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.LeaderboardEntity
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity

interface LeaderboardsRepository {
    fun getUserLeaderboard(
            type: LeaderboardEntity.Type?,
            range: LeaderboardEntity.Range?,
            timespan: LeaderboardEntity.TimeSpan?,
            companyId: Long?,
            page: Int?,
            limit: Int?
    ): Observable<List<UserRankingEntity>>
}
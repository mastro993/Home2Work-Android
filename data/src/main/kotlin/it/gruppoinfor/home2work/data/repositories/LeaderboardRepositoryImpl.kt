package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.LeaderboardsService
import it.gruppoinfor.home2work.data.entities.LeaderboardData
import it.gruppoinfor.home2work.data.mappers.UserRankingDataEntityMapper
import it.gruppoinfor.home2work.domain.entities.LeaderboardEntity
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.domain.interfaces.LeaderboardsRepository

class LeaderboardRepositoryImpl(
        private val userRankingMapper: UserRankingDataEntityMapper
) : LeaderboardsRepository {

    private val leaderboardService = APIService.get<LeaderboardsService>()

    override fun getUserLeaderboard(
            type: LeaderboardEntity.Type?,
            range: LeaderboardEntity.Range?,
            timespan: LeaderboardEntity.TimeSpan?,
            companyId: Long?,
            page: Int?,
            limit: Int?): Observable<List<UserRankingEntity>> {

        val typeData = type?.let { LeaderboardData.Type.valueOf(type.toString()) }
        val rangeData = range?.let { LeaderboardData.Range.valueOf(range.toString()) }
        val timespanData = timespan?.let { LeaderboardData.TimeSpan.valueOf(timespan.toString()) }

        return leaderboardService.getUserLeaderboard(typeData, rangeData, timespanData, companyId, page, limit)
                .map { it.map { userRankingMapper.mapFrom(it) } }


    }
}
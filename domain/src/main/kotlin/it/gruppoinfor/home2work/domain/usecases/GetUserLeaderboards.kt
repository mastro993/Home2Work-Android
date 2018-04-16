package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LeaderboardEntity
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.domain.interfaces.LeaderboardsRepository


class GetUserLeaderboards(
        transformer: Transformer<List<UserRankingEntity>>,
        private val leaderboardsRepository: LeaderboardsRepository
) : UseCase<List<UserRankingEntity>>(transformer) {

    companion object {
        private const val PARAM_TYPE = "param:type"
        private const val PARAM_RANGE = "param:range"
        private const val PARAM_TIME_SPAN = "param:timespan"
        private const val PARAM_COMPANY_ID = "param:companyId"
        private const val PARAM_PAGE = "param:page"
        private const val PARAM_LIMIT = "param:limit"
    }

    fun get(
            type: LeaderboardEntity.Type?,
            range: LeaderboardEntity.Range?,
            timeSpan: LeaderboardEntity.TimeSpan?,
            companyId: Long?,
            page: Int?,
            limit: Int?
    ): Observable<List<UserRankingEntity>> {
        val data = HashMap<String, Any>()

        type?.let { data[PARAM_TYPE] = it }
        range?.let { data[PARAM_RANGE] = it }
        timeSpan?.let { data[PARAM_TIME_SPAN] = it }
        companyId?.let { data[PARAM_COMPANY_ID] = it }
        page?.let { data[PARAM_PAGE] = it }
        limit?.let { data[PARAM_LIMIT] = it }

        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<UserRankingEntity>> {

        val type = data?.get(PARAM_TYPE)
        val range = data?.get(PARAM_RANGE)
        val timespan = data?.get(PARAM_TIME_SPAN)
        val companyId = data?.get(PARAM_COMPANY_ID)
        val page = data?.get(PARAM_PAGE)
        val limit = data?.get(PARAM_LIMIT)

        return leaderboardsRepository.getUserLeaderboard(
                type = type as LeaderboardEntity.Type?,
                range = range as LeaderboardEntity.Range?,
                timespan = timespan as LeaderboardEntity.TimeSpan?,
                companyId = companyId as Long?,
                page = page as Int?,
                limit = limit as Int?
        )

    }
}
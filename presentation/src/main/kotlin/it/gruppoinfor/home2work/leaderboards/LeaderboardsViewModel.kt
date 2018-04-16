package it.gruppoinfor.home2work.leaderboards

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.LeaderboardEntity
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.domain.usecases.GetUserLeaderboards
import it.gruppoinfor.home2work.entities.Leaderboard
import it.gruppoinfor.home2work.entities.UserRanking

class LeaderboardsViewModel(
        private val getUserLeaderboards: GetUserLeaderboards,
        private val userRankingMapper: Mapper<UserRankingEntity, UserRanking>
) : BaseViewModel() {

    private val resultsLimit = 20

    var viewState: MutableLiveData<LeaderboardsViewState> = MutableLiveData()

    init {
        viewState.value = LeaderboardsViewState()
    }

    fun getLeaderboard(type: Leaderboard.Type?, range: Leaderboard.Range?, timespan: Leaderboard.TimeSpan?, companyId: Long?, page: Int?) {

        val typeEntity = type?.let { LeaderboardEntity.Type.valueOf(it.toString()) }
        val rangeEntity = range?.let { LeaderboardEntity.Range.valueOf(it.toString()) }
        val timespanEntity = timespan?.let { LeaderboardEntity.TimeSpan.valueOf(it.toString()) }

        addDisposable(getUserLeaderboards.get(typeEntity, rangeEntity, timespanEntity, companyId, page, resultsLimit)
                .map { it.map { userRankingMapper.mapFrom(it) } }
                .subscribe({

                }, {

                }))

    }
}
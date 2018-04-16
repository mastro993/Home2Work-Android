package it.gruppoinfor.home2work.leaderboards

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.domain.usecases.GetUserLeaderboards
import it.gruppoinfor.home2work.entities.UserRanking

class LeaderboardsVMFactory(
        private val getUserLeaderboards: GetUserLeaderboards,
        private val userRankingMapper: Mapper<UserRankingEntity, UserRanking>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LeaderboardsViewModel(getUserLeaderboards, userRankingMapper) as T
    }
}
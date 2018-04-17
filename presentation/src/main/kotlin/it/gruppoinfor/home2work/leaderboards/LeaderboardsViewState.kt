package it.gruppoinfor.home2work.leaderboards

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.UserRanking

data class LeaderboardsViewState(
        val screenState: ScreenState = ScreenState.Loading,
        val isRefreshing: Boolean = false,
        val leaderboard: List<UserRanking> = listOf()
)
package it.gruppoinfor.home2work.leaderboards

import it.gruppoinfor.home2work.entities.UserRanking

data class LeaderboardsViewState(
        val isRefreshing: Boolean = false,
        val leaderboard: List<UserRanking> = listOf()
)
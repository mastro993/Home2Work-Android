package it.gruppoinfor.home2work.di.leaderboard

import dagger.Subcomponent
import it.gruppoinfor.home2work.leaderboards.LeaderboardsFragment

@LeaderboardScope
@Subcomponent(modules = [LeaderboardModule::class])
interface LeaderboardSubComponent {
    fun inject(leaderboardsFragment: LeaderboardsFragment)
}
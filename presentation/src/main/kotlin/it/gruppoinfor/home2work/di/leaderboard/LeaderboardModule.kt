package it.gruppoinfor.home2work.di.leaderboard

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.UserRankingEntityUserRankingMapper
import it.gruppoinfor.home2work.data.mappers.UserRankingDataEntityMapper
import it.gruppoinfor.home2work.data.repositories.LeaderboardRepositoryImpl
import it.gruppoinfor.home2work.domain.interfaces.LeaderboardsRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.domain.usecases.GetUserLeaderboards
import it.gruppoinfor.home2work.home.HomeVMFactory
import it.gruppoinfor.home2work.leaderboards.LeaderboardsVMFactory

@LeaderboardScope
@Module
class LeaderboardModule {

    @Provides
    fun provideLeaderboardsRepository(userRankingDataEntityMapper: UserRankingDataEntityMapper): LeaderboardsRepository {
        return LeaderboardRepositoryImpl(userRankingDataEntityMapper)
    }

    @Provides
    fun provideGetUserLeaderboardsUseCase(leaderboardsRepository: LeaderboardsRepository): GetUserLeaderboards {
        return GetUserLeaderboards(ASyncTransformer(), leaderboardsRepository)
    }

    @Provides
    fun provideLeaderboardsVMFactory(getUserLeaderboards: GetUserLeaderboards, userRankingEntityUserRankingMapper: UserRankingEntityUserRankingMapper): LeaderboardsVMFactory {
        return LeaderboardsVMFactory(getUserLeaderboards, userRankingEntityUserRankingMapper)
    }
}
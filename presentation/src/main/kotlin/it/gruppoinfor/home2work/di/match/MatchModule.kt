package it.gruppoinfor.home2work.di.match

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.MatchEntityMatchMapper
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository
import it.gruppoinfor.home2work.domain.usecases.EditMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatchList
import it.gruppoinfor.home2work.match.MatchVMFactory

@MatchScope
@Module
class MatchModule {


    @Provides
    fun provideGetMatchUseCase(matchRepository: MatchRepository): GetMatch {
        return GetMatch(ASyncTransformer(), matchRepository)
    }

    @Provides
    fun provideGetMatchListUseCase(matchRepository: MatchRepository): GetMatchList {
        return GetMatchList(ASyncTransformer(), matchRepository)
    }

    @Provides
    fun provideEditMatchUseCase(matchRepository: MatchRepository): EditMatch {
        return EditMatch(ASyncTransformer(), matchRepository)
    }

    @Provides
    fun provideMatchVMFactory(getMatch: GetMatch, getMatchList: GetMatchList, editMatch: EditMatch, mapper: MatchEntityMatchMapper): MatchVMFactory {
        return MatchVMFactory(getMatch, getMatchList, editMatch, mapper)
    }
}
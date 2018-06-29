package it.gruppoinfor.home2work.di.main

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.main.MainVMFactory
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ShareEntityShareMapper
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository
import it.gruppoinfor.home2work.domain.usecases.CreateShare
import it.gruppoinfor.home2work.domain.usecases.GetActiveShare
import it.gruppoinfor.home2work.domain.usecases.JoinShare


@Module
class MainModule {

    @Provides
    @MainScope
    fun provideGetActiveShareUseCase(shareRepository: ShareRepository): GetActiveShare {
        return GetActiveShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @MainScope
    fun provideJoinShareUseCase(shareRepository: ShareRepository): JoinShare {
        return JoinShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @MainScope
    fun provideCreateShareUseCase(shareRepository: ShareRepository): CreateShare {
        return CreateShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @MainScope
    fun provideMainVMFactory(getActiveShare: GetActiveShare, joinShare: JoinShare, createShare: CreateShare, shareMapper: ShareEntityShareMapper, localUserData: LocalUserData): MainVMFactory {
        return MainVMFactory(getActiveShare, joinShare, createShare, shareMapper, localUserData)
    }

}
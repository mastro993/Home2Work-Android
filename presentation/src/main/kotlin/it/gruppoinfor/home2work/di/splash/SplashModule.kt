package it.gruppoinfor.home2work.di.splash

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.UserEntityUserMapper
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetUser
import it.gruppoinfor.home2work.splash.SplashVMFactory

@SplashScope
@Module
class SplashModule {

    @Provides
    fun provideUserTokenLoginUseCase(userRepository: UserRepository): GetUser {
        return GetUser(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideSplashVMFactory(useCase: GetUser, mapper: UserEntityUserMapper, localUserData: LocalUserData): SplashVMFactory {
        return SplashVMFactory(useCase, mapper, localUserData)
    }
}
package it.gruppoinfor.home2work.di.signin

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.UserEntityUserMapper
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.UserLogin
import it.gruppoinfor.home2work.signin.SignInVMFactory


@Module
class SignInModule {
    @Provides
    @SignInScope
    fun provideUserLoginUseCase(userRepository: UserRepository): UserLogin {
        return UserLogin(ASyncTransformer(), userRepository)
    }

    @Provides
    @SignInScope
    fun provideAuthVMFactory(useCase: UserLogin, mapper: UserEntityUserMapper, localUserData: LocalUserData): SignInVMFactory {
        return SignInVMFactory(useCase, mapper, localUserData)
    }

}
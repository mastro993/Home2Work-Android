package it.gruppoinfor.home2work.di.user

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ProfileEntityProfileMapper
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetUserProfile
import it.gruppoinfor.home2work.user.UserVMFactory


@Module
class UserModule {

    @Provides
    @UserScope
    fun provideGetProfileUseCase(userRepository: UserRepository): GetUserProfile {
        return GetUserProfile(ASyncTransformer(), userRepository)
    }

    @Provides
    @UserScope
    fun provideProfileVMFactory(getUserProfile: GetUserProfile, mapper: ProfileEntityProfileMapper): UserVMFactory {
        return UserVMFactory(getUserProfile, mapper)
    }

}
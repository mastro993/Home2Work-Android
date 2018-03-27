package it.gruppoinfor.home2work.di.user

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ProfileEntityProfileMapper
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetUserProfile
import it.gruppoinfor.home2work.user.UserVMFactory


@Module
@UserScope
class UserModule {

    @Provides
    fun provideGetProfileUseCase(userRepository: UserRepository): GetUserProfile {
        return GetUserProfile(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideProfileVMFactory(getUserProfile: GetUserProfile, mapper: ProfileEntityProfileMapper): UserVMFactory {
        return UserVMFactory(getUserProfile, mapper)
    }

}
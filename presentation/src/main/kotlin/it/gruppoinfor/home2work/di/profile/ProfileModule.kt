package it.gruppoinfor.home2work.di.profile

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ProfileEntityProfileMapper
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetProfile
import it.gruppoinfor.home2work.domain.usecases.UpdateStatus
import it.gruppoinfor.home2work.profile.ProfileVMFactory

@ProfileScope
@Module
class ProfileModule {

    @Provides
    fun provideUpdateStatusUseCase(userRepository: UserRepository): UpdateStatus {
        return UpdateStatus(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideGetProfileUseCase(userRepository: UserRepository): GetProfile {
        return GetProfile(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideProfileVMFactory(getProfile: GetProfile, updateStatus: UpdateStatus, mapper: ProfileEntityProfileMapper): ProfileVMFactory {
        return ProfileVMFactory(getProfile, updateStatus, mapper)
    }

}
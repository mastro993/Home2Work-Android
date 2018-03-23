package it.gruppoinfor.home2work.di.profile

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.EditMatch
import it.gruppoinfor.home2work.domain.usecases.GetProfile
import it.gruppoinfor.home2work.mappers.ProfileEntityProfileMapper
import it.gruppoinfor.home2work.match.MatchVMFactory
import it.gruppoinfor.home2work.profile.ProfileVMFactory

@ProfileScope
@Module
class ProfileModule {

    @Provides
    fun provideEditMatchUseCase(userRepository: UserRepository): GetProfile {
        return GetProfile(ASyncTransformer(), userRepository)
    }

    @Provides
    fun provideMatchVMFactory(getProfile: GetProfile, mapper: ProfileEntityProfileMapper): ProfileVMFactory {
        return ProfileVMFactory(getProfile, mapper)
    }

}
package it.gruppoinfor.home2work.di.firebase

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken


@FirebaseScope
@Module
class FirebaseModule {

    @Provides
    fun provideStoreUserFCMToken(userRepository: UserRepository): StoreUserFCMToken {
        return StoreUserFCMToken(ASyncTransformer(), userRepository)
    }
}
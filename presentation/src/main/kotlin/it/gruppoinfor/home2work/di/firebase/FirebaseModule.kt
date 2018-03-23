package it.gruppoinfor.home2work.di.firebase

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.FirebaseTokenRepository
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import javax.inject.Singleton


@FirebaseScope
@Module
class FirebaseModule {

    @Provides
    fun provideStoreUserFCMToken(firebaseTokenRepository: FirebaseTokenRepository): StoreUserFCMToken {
        return StoreUserFCMToken(ASyncTransformer(), firebaseTokenRepository)
    }
}
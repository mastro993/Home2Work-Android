package it.gruppoinfor.home2work.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.common.mappers.UserEntityUserMapper
import it.gruppoinfor.home2work.common.mappers.UserUserEntityMapper
import it.gruppoinfor.home2work.data.mappers.*
import it.gruppoinfor.home2work.data.repositories.*
import it.gruppoinfor.home2work.domain.interfaces.*
import javax.inject.Singleton


@Module
class DataModule {


    @Singleton
    @Provides
    fun provideLocalUserData(preferencesRepository: PreferencesRepository, userEntityUserMapper: UserEntityUserMapper, userEntityMapper: UserUserEntityMapper): LocalUserData {
        return LocalUserData(preferencesRepository, userEntityUserMapper, userEntityMapper)
    }

    @Provides
    fun providePreferencesRepository(context: Context, userDataEntityMapper: UserDataEntityMapper, userEntityDataMapper: UserEntityDataMapper): PreferencesRepository {
        return PreferencesRepositoryImpl(context, userDataEntityMapper, userEntityDataMapper)
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
            userMapper: UserDataEntityMapper,
            profileMapper: ProfileDataEntityMapper
    ): UserRepository {
        return UserRepositoryImpl(userMapper, profileMapper)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
            chatDataEntityMapper: ChatDataEntityMapper,
            messageDataEntityMapper: ChatMessageDataEntityMapper
    ): ChatRepository {
        return ChatRepositoryImpl(chatDataEntityMapper, messageDataEntityMapper)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
            matchEntityDataMapper: MatchEntityDataMapper,
            matchDataEntityMapper: MatchDataEntityMapper
    ): MatchRepository {
        return MatchRepositoryImpl(matchDataEntityMapper, matchEntityDataMapper)
    }

    @Provides
    @Singleton
    fun provideShareRepository(
            shareDataEntityMapper: ShareDataEntityMapper,
            guestDataEntityMapper: GuestDataEntityMapper
    ): ShareRepository {
        return ShareRepositoryImpl(shareDataEntityMapper, guestDataEntityMapper)
    }


    @Provides
    @Singleton
    fun provideLocationRepository(
            context: Context,
            locationDataEntityMapper: UserLocationDataEntityMapper,
            locationEntityDataMapper: UserLocationEntityDataMapper
    ): LocationRepository {
        return LocationRepositoryImpl(context, locationDataEntityMapper, locationEntityDataMapper)
    }
}
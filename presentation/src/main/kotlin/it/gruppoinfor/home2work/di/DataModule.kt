package it.gruppoinfor.home2work.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.common.user.UserPreferences
import it.gruppoinfor.home2work.data.entities.MyObjectBox
import it.gruppoinfor.home2work.data.mappers.*
import it.gruppoinfor.home2work.data.repositories.*
import it.gruppoinfor.home2work.domain.interfaces.*
import javax.inject.Singleton


@Module
class DataModule {
    @Singleton
    @Provides
    fun provideObjectBoxDatabase(context: Context): BoxStore {
        return MyObjectBox.builder().androidContext(context).build()
    }

    @Singleton
    @Provides
    fun provideLocalUserData(userPreferences: UserPreferences, settingsPreferences: SettingsPreferences): LocalUserData {
        return LocalUserData(userPreferences, settingsPreferences)
    }

    @Provides
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Singleton
    @Provides
    fun provideSettingsPreferences(context: Context): SettingsPreferences {
        return SettingsPreferences(context)
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
            shareDataEntityMapper: ShareDataEntityMapper
    ): ShareRepository {
        return ShareRepositoryImpl(shareDataEntityMapper)
    }

    @Provides
    @Singleton
    fun provideFirebaseTokenRepository(): FirebaseTokenRepository {
        return FirebaseTokenRepositoryImpl()
    }
}
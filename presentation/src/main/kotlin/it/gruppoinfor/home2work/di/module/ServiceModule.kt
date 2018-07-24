package it.gruppoinfor.home2work.di.module

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.*


@Module
class ServiceModule {

    @Provides
    fun provideSaveLocationUseCase(locationRepository: LocationRepository): StoreUserLocation {
        return StoreUserLocation(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideGetUserLocationsUseCase(locationRepository: LocationRepository): GetUserLocations {
        return GetUserLocations(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideSyncUserLocationsUseCase(locationRepository: LocationRepository): SyncUserLocations {
        return SyncUserLocations(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideSyncUserLastLocationUseCase(locationRepository: LocationRepository): SyncUserLastLocation {
        return SyncUserLastLocation(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideDeleteUserLocationsUseCase(locationRepository: LocationRepository): DeleteUserLocations {
        return DeleteUserLocations(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideStoreUserFCMToken(userRepository: UserRepository): StoreUserFCMToken {
        return StoreUserFCMToken(ASyncTransformer(), userRepository)
    }


}
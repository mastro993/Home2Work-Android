package it.gruppoinfor.home2work.di.module

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.SaveUserLocation
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations


@Module
class ServiceModule {

    @Provides
    fun provideSaveLocationUseCase(locationRepository: LocationRepository): SaveUserLocation {
        return SaveUserLocation(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideGetUserLocationsUseCase(locationRepository: LocationRepository): GetUserLocations {
        return GetUserLocations(ASyncTransformer(), locationRepository)
    }

    @Provides
    fun provideSyncUserLocationsUseCase(locationRepository: LocationRepository): SyncUserLocations {
        return SyncUserLocations(ASyncTransformer(), locationRepository)
    }


}
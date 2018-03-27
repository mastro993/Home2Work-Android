package it.gruppoinfor.home2work.di

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository
import it.gruppoinfor.home2work.domain.usecases.UploadUserLocations
import javax.inject.Singleton


@Module
class ServiceModule {


    @Provides
    @Singleton
    fun provideUploadUserLocations(locationRepository: LocationRepository): UploadUserLocations {
        return UploadUserLocations(ASyncTransformer(), locationRepository)
    }


}
package it.gruppoinfor.home2work.data.repositories

import android.content.Context
import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.UserLocationService
import it.gruppoinfor.home2work.data.database.UserLocationDb
import it.gruppoinfor.home2work.data.mappers.UserLocationDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.UserLocationEntityDataMapper
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class LocationRepositoryImpl(
        private val context: Context,
        private val locationDataEntityMapper: UserLocationDataEntityMapper,
        private val locationEntityDataMapper: UserLocationEntityDataMapper
) : LocationRepository {

    private val locationService = APIService.get<UserLocationService>()

    override fun getUserLocations(userId: Long): Observable<List<UserLocationEntity>> {
        val db = UserLocationDb.getInstance(context)
        return db.userLocationDAO().getUserLocations(userId).map {
            it.map { locationDataEntityMapper.mapFrom(it) }
        }.toObservable()
    }

    override fun deleteUserLocations(userId: Long): Observable<Boolean> {
        val db = UserLocationDb.getInstance(context)
        return Observable.just(db.userLocationDAO().deleteUserLocations(userId) > 0)
    }

    override fun saveLocation(userLocation: UserLocationEntity): Observable<Long> {
        val db = UserLocationDb.getInstance(context)
        val userLocationData = locationEntityDataMapper.mapFrom(userLocation)
        return Observable.just(db.userLocationDAO().saveUserLocation(userLocationData))
    }

    override fun syncUserLocations(userLocations: List<UserLocationEntity>): Observable<Boolean> {
        val locations = userLocations.map { locationEntityDataMapper.mapFrom(it) }
        return locationService.uploadLocations(locations)
    }

    override fun syncUserLastLocation(userLocation: UserLocationEntity): Observable<Boolean> {
        val location = locationEntityDataMapper.mapFrom(userLocation)
        return locationService.uploadLastLocation(location)
    }
}
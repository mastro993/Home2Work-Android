package it.gruppoinfor.home2work.data.repositories

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIServiceGenerator
import it.gruppoinfor.home2work.data.api.getService
import it.gruppoinfor.home2work.data.api.services.UserLocationService
import it.gruppoinfor.home2work.data.entities.UserLocationData
import it.gruppoinfor.home2work.data.entities.UserLocationData_
import it.gruppoinfor.home2work.data.mappers.UserLocationDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.UserLocationEntityDataMapper
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.domain.interfaces.LocationRepository

class LocationRepositoryImpl(
        boxStore: BoxStore,
        private val locationDataEntityMapper: UserLocationDataEntityMapper,
        private val locationEntityDataMapper: UserLocationEntityDataMapper
) : LocationRepository {

    private val userLocationBox = boxStore.boxFor<UserLocationData>()
    private val locationService: UserLocationService = APIServiceGenerator.getService()

    override fun getUserLocations(userId: Long): Observable<List<UserLocationEntity>> {
        val query = userLocationBox.query().equal(UserLocationData_.userId, userId).build()
        return RxQuery.observable(query).map {
            it.map { locationDataEntityMapper.mapFrom(it) }
        }
    }

    override fun saveLocation(userLocation: UserLocationEntity): Observable<Long> {
        val location = locationEntityDataMapper.mapFrom(userLocation)
        return Observable.just(userLocationBox.put(location))
    }

    override fun syncUserLocations(userLocations: List<UserLocationEntity>): Observable<Boolean> {
        val locations = userLocations.map { locationEntityDataMapper.mapFrom(it) }
        return locationService.uploadLocations(locations)
    }
}
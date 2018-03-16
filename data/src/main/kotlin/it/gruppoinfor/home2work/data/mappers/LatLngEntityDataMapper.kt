package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.LatLngData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LatLngEntityDataMapper @Inject constructor() : Mapper<LatLngEntity, LatLngData>() {
    override fun mapFrom(from: LatLngEntity): LatLngData {
        return LatLngData(
                lat = from.latitude,
                lng = from.longitude
        )
    }

}
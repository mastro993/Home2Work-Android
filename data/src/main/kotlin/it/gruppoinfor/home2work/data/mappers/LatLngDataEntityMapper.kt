package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.LatLngData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LatLngDataEntityMapper @Inject constructor() : Mapper<LatLngData, LatLngEntity>() {
    override fun mapFrom(from: LatLngData): LatLngEntity {
        return LatLngEntity(
                latitude = from.lat,
                longitude = from.lng
        )
    }
}
package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.entities.UserLocation
import javax.inject.Inject


class UserLocationUserLocationEntityMapper @Inject constructor() : Mapper<UserLocation, UserLocationEntity>() {


    override fun mapFrom(from: UserLocation): UserLocationEntity {
        return UserLocationEntity(
                userId = from.userId,
                latitude = from.latitude,
                longitude = from.longitude,
                date = from.date
        )

    }
}
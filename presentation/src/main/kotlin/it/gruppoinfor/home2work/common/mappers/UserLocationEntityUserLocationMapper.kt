package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import it.gruppoinfor.home2work.entities.UserLocation
import javax.inject.Inject


class UserLocationEntityUserLocationMapper @Inject constructor() : Mapper<UserLocationEntity, UserLocation>() {


    override fun mapFrom(from: UserLocationEntity): UserLocation {
        return UserLocation(
                userId = from.userId,
                latitude = from.latitude,
                longitude = from.longitude,
                date = from.date,
                type = from.type
        )

    }
}
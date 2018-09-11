package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.UserLocationData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity
import javax.inject.Inject

class UserLocationEntityDataMapper @Inject constructor() : Mapper<UserLocationEntity, UserLocationData>() {
    override fun mapFrom(from: UserLocationEntity): UserLocationData {
        return UserLocationData(
                userId = from.userId,
                latitude = from.latitude,
                longitude = from.longitude,
                date = from.date,
                type = from.type
        )
    }
}
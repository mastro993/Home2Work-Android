package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.GuestData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.domain.entities.GuestStatusEntity
import javax.inject.Inject


class GuestDataEntityMapper @Inject constructor() : Mapper<GuestData, GuestEntity>() {
    override fun mapFrom(from: GuestData): GuestEntity {
        val user = UserDataEntityMapper().mapFrom(from.user)
        val status = GuestStatusEntity.valueOf(from.status.toString())

        return GuestEntity(
                id = from.id,
                shareId = from.shareId,
                user = user,
                startLat = from.startLat,
                startLng = from.startLng,
                endLat = from.endLat,
                endLng = from.endLng,
                status = status,
                distance = from.distance
        )
    }
}
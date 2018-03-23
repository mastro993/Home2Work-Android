package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.GuestData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.domain.entities.GuestStatusEntity
import javax.inject.Inject
import javax.inject.Singleton



class GuestDataEntityMapper @Inject constructor() : Mapper<GuestData, GuestEntity>() {
    override fun mapFrom(from: GuestData): GuestEntity {
        val user = from.user?.let {
            UserDataEntityMapper().mapFrom(it)
        }
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
package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.GuestData
import it.gruppoinfor.home2work.data.entities.GuestStatusData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import javax.inject.Inject
import javax.inject.Singleton



class GuestEntityDataMapper @Inject constructor() : Mapper<GuestEntity, GuestData>() {
    override fun mapFrom(from: GuestEntity): GuestData {
        val user = from.user?.let {
            UserEntityDataMapper().mapFrom(it)
        }

        val status = GuestStatusData.valueOf(from.status.toString())

        return GuestData(
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
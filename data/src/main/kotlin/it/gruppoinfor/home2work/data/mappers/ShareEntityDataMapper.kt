package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ShareData
import it.gruppoinfor.home2work.data.entities.ShareStatusData
import it.gruppoinfor.home2work.data.entities.ShareTypeData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import javax.inject.Inject


class ShareEntityDataMapper @Inject constructor() : Mapper<ShareEntity, ShareData>() {
    override fun mapFrom(from: ShareEntity): ShareData {
        val host = UserEntityDataMapper().mapFrom(from.host)

        val status = ShareStatusData.from(from.status.value)
        val type = ShareTypeData.from(from.type.value)

        val guests = from.guests.map { GuestEntityDataMapper().mapFrom(it) } as ArrayList

        return ShareData(
                id = from.id,
                host = host,
                status = status,
                date = from.date,
                type = type,
                guests = guests,
                startLat = from.startLat,
                startLng = from.startLng,
                endLat = from.endLat,
                endLng = from.endLng,
                sharedDistance = from.sharedDistance
        )
    }
}
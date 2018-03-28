package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.entities.Guest
import it.gruppoinfor.home2work.entities.GuestStatus
import javax.inject.Inject


class GuestEntityGuestMapper @Inject constructor() : Mapper<GuestEntity, Guest>() {

    override fun mapFrom(from: GuestEntity): Guest {

        val user = UserEntityUserMapper().mapFrom(from.user)

        val status = GuestStatus.valueOf(from.status.toString())

        return Guest(
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
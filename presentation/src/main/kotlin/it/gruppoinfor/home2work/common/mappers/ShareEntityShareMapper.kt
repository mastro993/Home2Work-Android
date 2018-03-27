package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.ShareStatus
import it.gruppoinfor.home2work.entities.ShareType
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ShareEntityShareMapper @Inject constructor() : Mapper<ShareEntity, Share>() {

    override fun mapFrom(from: ShareEntity): Share {

        val host = UserEntityUserMapper().mapFrom(from.host)

        val status = ShareStatus.from(from.status.value)
        val type = ShareType.from(from.type.value)

        val guests = from.guests.map { GuestEntityGuestMapper().mapFrom(it) } as ArrayList

        return Share(
                id = from.id,
                host = host,
                status = status,
                date = from.date,
                type = type,
                guests = guests
        )
    }

}
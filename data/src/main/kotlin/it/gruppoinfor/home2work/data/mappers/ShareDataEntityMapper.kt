package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ShareData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.entities.ShareStatusEntity
import it.gruppoinfor.home2work.domain.entities.ShareTypeEntity
import javax.inject.Inject


class ShareDataEntityMapper @Inject constructor() : Mapper<ShareData, ShareEntity>() {
    override fun mapFrom(from: ShareData): ShareEntity {
        val host = UserDataEntityMapper().mapFrom(from.host)
        val status = ShareStatusEntity.from(from.status.value)
        val type = ShareTypeEntity.from(from.type.value)


        val guests = from.guests.map { GuestDataEntityMapper().mapFrom(it) } as ArrayList

        return ShareEntity(
                id = from.id,
                host = host,
                status = status,
                date = from.date,
                type = type,
                guests = guests
        )
    }
}
package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ShareData
import it.gruppoinfor.home2work.data.entities.ShareStatusData
import it.gruppoinfor.home2work.data.entities.ShareTypeData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import javax.inject.Inject
import javax.inject.Singleton



class ShareEntityDataMapper @Inject constructor() : Mapper<ShareEntity, ShareData>() {
    override fun mapFrom(from: ShareEntity): ShareData {
        val host = from.host?.let {
            UserEntityDataMapper().mapFrom(it)
        }

        val status = ShareStatusData.from(from.status.value)
        val type = ShareTypeData.from(from.type.value)

        val guests = from.guests.map { GuestEntityDataMapper().mapFrom(it) } as ArrayList

        return ShareData(
                id = from.id,
                host = host,
                status = status,
                date = from.date,
                type = type,
                guests = guests
        )
    }
}
package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class ShareEntity(
        val id: Long,
        val host: UserEntity,
        val status: ShareStatusEntity,
        val date: Date?,
        val type: ShareTypeEntity,
        val guests: ArrayList<GuestEntity>,
        val sharedDistance: Int
)

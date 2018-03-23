package it.gruppoinfor.home2work.domain.entities

import java.util.*
import kotlin.collections.ArrayList

data class ShareEntity(
        val id: Long,
        val host: UserEntity?,
        val status: ShareStatusEntity,
        val date: Date?,
        val type: ShareTypeEntity,
        val guests: ArrayList<GuestEntity>
)

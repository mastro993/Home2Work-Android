package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class ShareEntity(
        val id: Long,
        val host: UserEntity,
        val status: Status,
        val date: Date,
        val type: Type,
        val guests: List<GuestEntity>
) {

    enum class Status constructor(val value: Int) {
        CREATED(0),
        COMPLETED(1),
        CANCELED(2)
    }

    enum class Type constructor(val value: Int) {
        DRIVER(0),
        GUEST(1)
    }
}

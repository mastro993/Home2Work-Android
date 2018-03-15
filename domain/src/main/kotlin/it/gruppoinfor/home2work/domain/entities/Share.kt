package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class Share(

        val id: Long,
        val host: User,
        val status: Status,
        val date: Date,
        val type: Type,
        val guests: ArrayList<Guest>

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

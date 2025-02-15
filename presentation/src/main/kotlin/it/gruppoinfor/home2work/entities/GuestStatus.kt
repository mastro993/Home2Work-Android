package it.gruppoinfor.home2work.entities

import java.io.Serializable


enum class GuestStatus constructor(val value: Int) : Serializable {
    JOINED(0),
    COMPLETED(1),
    LEAVED(2);

    companion object {
        fun from(findValue: Int): GuestStatus = GuestStatus.values().first { it.value == findValue }
    }


}
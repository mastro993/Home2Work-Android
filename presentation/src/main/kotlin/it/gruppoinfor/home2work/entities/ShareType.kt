package it.gruppoinfor.home2work.entities

import it.gruppoinfor.home2work.data.entities.ShareTypeData


enum class ShareType constructor(val value: Int) {
    HOST(0),
    GUEST(1);

    companion object {
        fun from(findValue: Int): ShareType = ShareType.values().first { it.value == findValue }
    }
}
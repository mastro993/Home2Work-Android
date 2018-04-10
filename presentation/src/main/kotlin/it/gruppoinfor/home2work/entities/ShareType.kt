package it.gruppoinfor.home2work.entities

import java.io.Serializable


enum class ShareType constructor(val value: Int) : Serializable {
    HOST(0),
    GUEST(1);

    companion object {
        fun from(findValue: Int): ShareType = ShareType.values().first { it.value == findValue }
    }
}
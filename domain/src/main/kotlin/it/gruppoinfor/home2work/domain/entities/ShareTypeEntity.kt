package it.gruppoinfor.home2work.domain.entities


enum class ShareTypeEntity constructor(val value: Int) {
    HOST(0),
    GUEST(1);

    companion object {
        fun from(findValue: Int): ShareTypeEntity = ShareTypeEntity.values().first { it.value == findValue }
    }
}
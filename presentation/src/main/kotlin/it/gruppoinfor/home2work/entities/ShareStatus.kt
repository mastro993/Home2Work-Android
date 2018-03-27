package it.gruppoinfor.home2work.entities


enum class ShareStatus constructor(val value: Int) {
    CREATED(0),
    COMPLETED(1),
    CANCELED(2);

    companion object {
        fun from(findValue: Int): ShareStatus = ShareStatus.values().first { it.value == findValue }
    }


}
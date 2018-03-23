package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


enum class ShareTypeData constructor(val value: Int) {
    @SerializedName("0")
    HOST(0),
    @SerializedName("1")
    GUEST(1);

    companion object {
        fun from(findValue: Int): ShareTypeData = ShareTypeData.values().first { it.value == findValue }
    }
}
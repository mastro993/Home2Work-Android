package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


enum class GuestStatusData constructor(val value: Int) {
    @SerializedName("0")
    JOINED(0),
    @SerializedName("1")
    COMPLETED(1),
    @SerializedName("2")
    LEAVED(2);

    companion object {
        fun from(findValue: Int): GuestStatusData = GuestStatusData.values().first { it.value == findValue }
    }
}
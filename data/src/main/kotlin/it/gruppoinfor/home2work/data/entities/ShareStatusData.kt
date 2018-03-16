package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


enum class ShareStatusData constructor(val value: Int) {
    @SerializedName("0")
    CREATED(0),
    @SerializedName("1")
    COMPLETED(1),
    @SerializedName("2")
    CANCELED(2)
}
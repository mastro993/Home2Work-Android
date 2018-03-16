package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


data class SharingActivityData(
        @SerializedName("Shares") var shares: Int = -1,
        @SerializedName("Distance") var distance: Int = -1
)
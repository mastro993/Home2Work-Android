package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class GuestData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("ShareId") var shareId: Long = -1,
        @SerializedName("User") var user: UserData,
        @SerializedName("StartLat") var startLat: Double,
        @SerializedName("StartLng") var startLng: Double,
        @SerializedName("EndLat") var endLat: Double? = null,
        @SerializedName("EndLng") var endLng: Double? = null,
        @SerializedName("Status") var status: GuestStatusData? = null,
        @SerializedName("Distance") var distance: Int = -1
)

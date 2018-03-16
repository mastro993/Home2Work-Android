package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class GuestData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("ShareId") var shareId: Long = -1,
        @SerializedName("User") var user: UserData? = null,
        @SerializedName("StartLocation") var startLocation: LatLngData? = null,
        @SerializedName("EndLocation") var endLocation: LatLngData? = null,
        @SerializedName("Status") var status: ShareStatusData? = null,
        @SerializedName("Distance") var distance: Int = -1
)

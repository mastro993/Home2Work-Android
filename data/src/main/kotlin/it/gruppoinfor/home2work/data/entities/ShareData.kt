package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class ShareData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("Host") var host: UserData,
        @SerializedName("Status") var status: ShareStatusData,
        @SerializedName("Time") var date: Date? = null,
        @SerializedName("Type") var type: ShareTypeData,
        @SerializedName("Guests") var guests: ArrayList<GuestData> = ArrayList(),
        @SerializedName("StartLat") var startLat: Double,
        @SerializedName("StartLng") var startLng: Double,
        @SerializedName("EndLat") var endLat: Double? = null,
        @SerializedName("EndLng") var endLng: Double? = null,
        @SerializedName("SharedDistance") var sharedDistance: Int = 0
)

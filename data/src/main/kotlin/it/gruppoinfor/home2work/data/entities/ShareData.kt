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
        @SerializedName("Guests") var guests: ArrayList<GuestData> = ArrayList()
)

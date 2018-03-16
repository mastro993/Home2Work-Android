package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class ShareData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("Host") var host: UserData? = null,
        @SerializedName("Status") var status: ShareStatusData? = null,
        @SerializedName("Time") var date: Date? = null,
        @SerializedName("Type") var type: ShareTypeData? = null,
        @SerializedName("Guests") var guests: ArrayList<Guest> = ArrayList()
)

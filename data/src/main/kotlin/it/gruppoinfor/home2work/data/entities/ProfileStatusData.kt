package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class ProfileStatusData(
        @SerializedName("status") var status: String? = null,
        @SerializedName("date") var date: Date? = null
)
package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class ProfileStatusData(
        @SerializedName("Status") var status: String,
        @SerializedName("Date") var date: Date
)
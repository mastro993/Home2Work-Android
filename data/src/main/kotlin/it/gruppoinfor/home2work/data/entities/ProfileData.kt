package it.gruppoinfor.home2work.data.entities


import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.HashMap

data class ProfileData(
        @SerializedName("Status") var status: ProfileStatusData?,
        @SerializedName("Karma") var karma: KarmaData,
        @SerializedName("Stats") var stats: StatisticsData,
        @SerializedName("Activity") var activity: Map<Int, SharingActivityData> = HashMap(),
        @SerializedName("Regdate") var regdate: Date
)
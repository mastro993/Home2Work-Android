package it.gruppoinfor.home2work.data.entities


import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.HashMap

data class UserProfileData(
        @SerializedName("Exp") var exp: UserExperienceData? = null,
        @SerializedName("Stats") var stats: UserStatsData? = null,
        @SerializedName("Activity") var activity: Map<Int, SharingActivityData> = HashMap(),
        @SerializedName("Regdate") var regdate: Date? = null
)
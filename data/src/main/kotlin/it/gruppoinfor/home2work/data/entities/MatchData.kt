package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class MatchData(
        @SerializedName("MatchId") var id: Long = -1,
        @SerializedName("Host") var host: UserData,
        @SerializedName("HomeScore") var homeScore: Int? = null,
        @SerializedName("JobScore") var jobScore: Int? = null,
        @SerializedName("TimeScore") var timeScore: Int? = null,
        @SerializedName("ArrivalTime") var arrivalTime: Date? = null,
        @SerializedName("DepartureTime") var departureTime: Date? = null,
        @SerializedName("Distance") var distance: Int? = null,
        @SerializedName("IsNew") var isNew: Boolean = true,
        @SerializedName("IsHidden") var isHidden: Boolean = false
)

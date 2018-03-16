package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class MatchData(
        @SerializedName("MatchId") var matchId: Long? = -1,
        @SerializedName("Host") var host: UserData? = null,
        @SerializedName("HomeScore") var homeScore: Int? = -1,
        @SerializedName("JobScore") var jobScore: Int? = -1,
        @SerializedName("TimeScore") var timeScore: Int? = -1,
        @SerializedName("ArrivalTime") var arrivalTime: Date? = null,
        @SerializedName("DepartureTime") var departureTime: Date? = null,
        @SerializedName("Distance") var distance: Int = -1,
        @SerializedName("IsNew") var isNew: Boolean = true,
        @SerializedName("IsHidden") var isHidden: Boolean = true
)

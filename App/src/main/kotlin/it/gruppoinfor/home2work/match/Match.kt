package it.gruppoinfor.home2work.match

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2work.user.User
import java.io.Serializable
import java.util.*

class Match : Serializable {

    @SerializedName("MatchId")
    @Expose
    var matchId: Long = 0L
    @SerializedName("Host")
    @Expose
    var host: User? = null
    @SerializedName("HomeScore")
    @Expose
    var homeScore: Int = 0
    @SerializedName("JobScore")
    @Expose
    var jobScore: Int = 0
    @SerializedName("TimeScore")
    @Expose
    var timeScore: Int = 0
    @SerializedName("ArrivalTime")
    @Expose
    var arrivalTime: Date? = null
    @SerializedName("DepartureTime")
    @Expose
    var departureTime: Date? = null
    @SerializedName("Distance")
    @Expose
    var distance: Int = 0
    @SerializedName("IsNew")
    @Expose
    var isNew: Boolean = true
    @SerializedName("IsHidden")
    @Expose
    var isHidden: Boolean = true

    fun getScore(): Int {
        return (homeScore + jobScore + timeScore)
                .div(3.0)
                .toInt()
    }

}

package it.gruppoinfor.home2workapi.match

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.common.LatLng
import it.gruppoinfor.home2workapi.user.User
import java.io.Serializable
import java.sql.Timestamp
import java.util.*

class Match : Serializable {

    @SerializedName("MatchId")
    @Expose
    var matchID: Long = 0
    @SerializedName("Guest")
    @Expose
    var guest: User? = null
    @SerializedName("Host")
    @Expose
    var host: User? = null
    @SerializedName("Weekdays")
    @Expose
    var weekdays: ArrayList<Int> = ArrayList(0)
    @SerializedName("Score")
    @Expose
    var score: Int = 0
    @SerializedName("Distance")
    @Expose
    var distance: Int = 0
    @SerializedName("StartLocation")
    @Expose
    var startLocation: LatLng = LatLng()
    @SerializedName("StartTime")
    @Expose
    var startTime: Timestamp = Timestamp(0)
    @SerializedName("EndLocation")
    @Expose
    var endLocation: LatLng = LatLng()
    @SerializedName("EndTime")
    @Expose
    var endTime: Timestamp = Timestamp(0)
    @SerializedName("New")
    @Expose
    var isNew: Boolean = true
    @SerializedName("Hidden")
    @Expose
    var hidden: Boolean = false
}

package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.sql.Timestamp

class Guest : Serializable {

    @SerializedName("ShareId")
    @Expose
    var shareId: Long = 0
    @SerializedName("User")
    @Expose
    var user: User = User()
    @SerializedName("StartLocation")
    @Expose
    var startLocation: LatLng = LatLng()
    @SerializedName("StartDate")
    @Expose
    var startTime: Timestamp = Timestamp(0)
    @SerializedName("EndLocation")
    @Expose
    var endLocation: LatLng = LatLng()
    @SerializedName("EndDate")
    @Expose
    var endTime: Timestamp = Timestamp(0)
    @SerializedName("Status")
    @Expose
    var status: Status? = null
    @SerializedName("Distance")
    @Expose
    var distance: Int = 0

    enum class Status private constructor(val value: Int) {

        @SerializedName("0")
        JOINED(0),
        @SerializedName("1")
        COMPLETED(1),
        @SerializedName("2")
        CANCELED(2)
    }
}

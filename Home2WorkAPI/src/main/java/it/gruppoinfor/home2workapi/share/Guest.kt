package it.gruppoinfor.home2workapi.share

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.common.LatLng
import it.gruppoinfor.home2workapi.user.User
import java.io.Serializable

class Guest : Serializable {

    @SerializedName("ShareId")
    @Expose
    var shareId: Long = 0
    @SerializedName("User")
    @Expose
    var user: User? = null
    @SerializedName("StartLocation")
    @Expose
    var startLocation: LatLng = LatLng()
    @SerializedName("EndLocation")
    @Expose
    var endLocation: LatLng = LatLng()
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

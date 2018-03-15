package it.gruppoinfor.home2work.location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


class Location : Serializable {

    @SerializedName("latlng")
    @Expose
    var latLng: LatLng = LatLng()
    @SerializedName("timestamp")
    @Expose
    var date: Date = Date()
}
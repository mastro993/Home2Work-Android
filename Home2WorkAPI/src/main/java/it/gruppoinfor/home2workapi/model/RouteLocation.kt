package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.Date


class RouteLocation : Serializable {

    @SerializedName("latlng")
    @Expose
    var latLng: LatLng = LatLng()
    @SerializedName("timestamp")
    @Expose
    var date: Date = Date()
}
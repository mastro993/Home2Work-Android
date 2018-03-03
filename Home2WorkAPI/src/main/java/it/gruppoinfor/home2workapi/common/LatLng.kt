package it.gruppoinfor.home2workapi.common

import android.location.Location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable


class LatLng : Serializable {

    @SerializedName("Latitude")
    @Expose
    var lat: Double = 0.0
    @SerializedName("Longitude")
    @Expose
    var lng: Double = 0.0

    constructor() {
        // Empty constructor
    }
    
    constructor(lat: Double, lng: Double) {
        this.lat = lat
        this.lng = lng
    }

    fun distanceTo(latLng: LatLng): Float {
        val dep = Location("")
        dep.latitude = lat
        dep.longitude = lng
        val dest = Location("")
        dest.latitude = latLng.lat
        dest.longitude = latLng.lng
        return dep.distanceTo(dest)
    }

    fun distanceTo(dest: Location): Float {
        val dep = Location("")
        dep.latitude = lat
        dep.longitude = lng
        return dep.distanceTo(dest)
    }
}

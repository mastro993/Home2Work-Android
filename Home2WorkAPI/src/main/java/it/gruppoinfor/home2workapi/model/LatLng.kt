package it.gruppoinfor.home2workapi.model

import android.arch.persistence.room.Ignore
import android.location.Location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable


class LatLng : Serializable {

    @SerializedName("latitude")
    @Expose
    var lat: Double = 0.0
    @SerializedName("longitude")
    @Expose
    var lng: Double = 0.0

    constructor() {
        // Empty constructor
    }

    @Ignore
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

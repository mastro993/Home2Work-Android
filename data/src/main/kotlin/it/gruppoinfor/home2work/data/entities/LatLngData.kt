package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


data class LatLngData(
        @SerializedName("Latitude") var lat: Double = 0.0,
        @SerializedName("Longitude") var lng: Double = 0.0
)

package it.gruppoinfor.home2work.utils

import android.arch.persistence.room.TypeConverter


import java.util.Locale

import it.gruppoinfor.home2workapi.model.LatLng

object LatLngConverter {

    @TypeConverter
    fun fromString(stringLatLng: String): LatLng {
        val strings = stringLatLng.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val lat = java.lang.Double.parseDouble(strings[0])
        val lng = java.lang.Double.parseDouble(strings[1])
        return LatLng(lat, lng)
    }

    @TypeConverter
    fun toString(latLng: LatLng): String {
        return String.format(Locale.ROOT, "%1$.8f,%2$.8f", latLng.lat, latLng.lng)
    }
}

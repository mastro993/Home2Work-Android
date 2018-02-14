package it.gruppoinfor.home2work.utils


import android.arch.persistence.room.TypeConverter
import it.gruppoinfor.home2workapi.model.LatLng

class LatLngConverter {

    @TypeConverter
    fun fromString(stringLatLng: String): LatLng {
        val strings = stringLatLng.split(",")
        val lat = strings[0].toDouble()
        val lng = strings[1].toDouble()
        return LatLng(lat, lng)
    }

    @TypeConverter
    fun toString(latLng: LatLng): String {
        //return "${latLng.lat},${latLng.lng}"
        return "%.8f,%.8f".format(latLng.lat, latLng.lng)
        //return String.format(Locale.ROOT, "%1$.8f,%2$.8f", latLng.lat, latLng.lng)
    }
}

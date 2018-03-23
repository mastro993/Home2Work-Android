package it.gruppoinfor.home2work.extensions

import com.google.android.gms.maps.model.LatLng

/**
 * Created by feder on 19/03/2018.
 */

fun LatLng.distanceTo(toLatLng: LatLng): Double {
    return this.distanceTo(toLatLng.latitude, toLatLng.longitude)
}

fun LatLng.distanceTo(endLat: Double, endLng: Double): Double {
    val startLat = this.latitude
    val startLng = this.longitude

    val r = 6372.8 // In kilometers
    val dLat = (endLat - startLat).toRadians()
    val dLon = (endLng - startLng).toRadians()
    val lat1 = startLat.toRadians()
    val lat2 = endLat.toRadians()

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
    var c = 2 * Math.asin(Math.sqrt(a));
    return r * 2 * Math.asin(Math.sqrt(a))
}

fun Double.toRadians(): Double {
    return Math.PI * this / 180.0
}
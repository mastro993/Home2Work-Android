package it.gruppoinfor.home2work.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds


object RouteUtils {

    fun getRouteBounds(locations: List<LatLng>): LatLngBounds {

        val builder = LatLngBounds.Builder()

        for (location in locations) {
            builder.include(location)
        }

        return builder.build()
    }
}

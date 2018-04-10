package it.gruppoinfor.home2work.common.utilities

import it.gruppoinfor.home2work.entities.Share

/**
 * Helper per la creazione di URL per le Static Map di Google
 */
object StaticMapUriBuilder {

    private const val base = "https://maps.googleapis.com/maps/api/staticmap?"
    private const val API_KEY = "AIzaSyCIpRESm_T2CkxtLNE7ViFEkKafbQxMl9E"

    private const val width: Int = 128
    private const val height: Int = 72

    fun buildFor(share: Share): String {

        val size = sizes(5)
        val hostStart = marker(share.startLat, share.startLng, "blue")
        val hostEnd = marker(share.endLat!!, share.endLng!!, "blue")

        return "$base$size&$hostStart&$hostEnd&key=$API_KEY"

    }

    private fun sizes(factor: Int) : String{
        return "size=${width * factor}x${height * factor}"
    }

    private fun marker(latitude: Double, longitude: Double, color: String, label: String? = null): String {

        return "markers=color:$color|$latitude,$longitude"

    }

}
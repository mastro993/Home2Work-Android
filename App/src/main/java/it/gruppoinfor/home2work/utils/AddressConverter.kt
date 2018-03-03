package it.gruppoinfor.home2work.utils

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2workapi.common.LatLng
import java.io.IOException
import java.util.*


object AddressConverter {

    fun addressToLatLng(context: Context, addr: String, onSuccessListener: OnSuccessListener<LatLng>, onFailureListener: OnFailureListener) {

        val geocoder = Geocoder(context, Locale.ITALY)
        var lat = 0.0
        var lon = 0.0

        try {
            val addressList = geocoder.getFromLocationName(addr, 1)
            if (addressList.size > 0) {
                val address = addressList[0]
                lat = address.latitude
                lon = address.longitude
            } else {
                onFailureListener.onFailure(Exception("Indirizzo inesistente"))
            }
        } catch (e: IOException) {
            onFailureListener.onFailure(e)
        }

        onSuccessListener.onSuccess(LatLng(lat, lon))

    }
}
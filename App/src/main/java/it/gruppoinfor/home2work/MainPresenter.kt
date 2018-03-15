package it.gruppoinfor.home2work

import android.location.Location
import it.gruppoinfor.home2workapi.LatLng

interface MainPresenter {
    fun onResume()
    fun newShare()
    fun joinShare(shareId: Long, hostLatLng: LatLng, joinLocation: Location)
    fun getOngoingShare()
    fun onPause()
}
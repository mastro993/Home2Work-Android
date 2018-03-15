package it.gruppoinfor.home2work.shares

import android.graphics.Bitmap
import android.location.Location
import it.gruppoinfor.home2workapi.LatLng

interface OngoingSharePresenter {
    fun onResume()
    fun onPause()
    fun refreshGuests()
    fun banGuest(guestId: Long)
    fun cancelOngoingShare()
    fun leaveOngoingShare()
    fun finishShare()
    fun getShareQRCode(joinLocation: Location): Bitmap?
    fun completeShare(hostLatLng: LatLng, endLocation: Location)
}
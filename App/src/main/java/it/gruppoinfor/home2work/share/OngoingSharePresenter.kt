package it.gruppoinfor.home2work.share

import android.graphics.Bitmap
import android.location.Location
import it.gruppoinfor.home2work.location.LatLng

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
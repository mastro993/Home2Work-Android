package it.gruppoinfor.home2work.sharecurrent

import android.graphics.Bitmap
import android.location.Location

interface OngoingSharePresenter {
    fun onResume()
    fun onPause()
    fun refreshGuests()
    fun banGuest(guestId: Long)
    fun cancelOngoingShare()
    fun leaveOngoingShare()
    fun finishShare()
    fun getShareQRCode(joinLocation: Location): Bitmap?
    fun completeShare(hostLocation: Location, endLocation: Location)
}
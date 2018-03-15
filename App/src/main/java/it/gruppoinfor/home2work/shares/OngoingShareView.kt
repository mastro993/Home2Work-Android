package it.gruppoinfor.home2work.shares

import it.gruppoinfor.home2workapi.share.Guest
import it.gruppoinfor.home2workapi.share.Share

interface OngoingShareView {
    fun setGuests(share: Share)
    fun onGuestBanned()
    fun onGuestClick(position: Int, guest: Guest)
    fun onGuestLongClick(position: Int, guest: Guest)
    fun showErrorMessage(errorMessage: String)
    fun onShareCanceled()
    fun onShareCompleted()
}
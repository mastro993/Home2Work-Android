package it.gruppoinfor.home2work.sharecurrent

import it.gruppoinfor.home2work.entities.Guest
import it.gruppoinfor.home2work.entities.Share

interface OngoingShareView {
    fun setGuests(share: Share)
    fun onGuestBanned()
    fun onGuestClick(position: Int, guest: Guest)
    fun onGuestLongClick(position: Int, guest: Guest)
    fun showErrorMessage(errorMessage: String)
    fun onShareCanceled()
    fun onShareCompleted()
}
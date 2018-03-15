package it.gruppoinfor.home2work.share

interface OngoingShareView {
    fun setGuests(share: Share)
    fun onGuestBanned()
    fun onGuestClick(position: Int, guest: Guest)
    fun onGuestLongClick(position: Int, guest: Guest)
    fun showErrorMessage(errorMessage: String)
    fun onShareCanceled()
    fun onShareCompleted()
}
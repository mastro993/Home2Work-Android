package it.gruppoinfor.home2work

interface MainView {
    fun onShareCreated()
    fun onShareJoined()
    fun onOngoingShareRefresh()
    fun showError(errorMessage: String)
}
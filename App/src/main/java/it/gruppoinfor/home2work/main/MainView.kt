package it.gruppoinfor.home2work.main

interface MainView {
    fun onShareCreated()
    fun onShareJoined()
    fun onOngoingShareRefresh()
    fun showError(errorMessage: String)
}
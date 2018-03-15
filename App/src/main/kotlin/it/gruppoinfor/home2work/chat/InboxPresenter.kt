package it.gruppoinfor.home2work.chat


interface InboxPresenter {

    fun onCreate()

    fun onResume()

    fun onRefresh()

    fun onMessage()

    fun onPause()

}
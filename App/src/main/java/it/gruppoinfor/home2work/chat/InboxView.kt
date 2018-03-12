package it.gruppoinfor.home2work.chat

import it.gruppoinfor.home2workapi.chat.Chat


interface InboxView {
    fun setItems(list: List<Chat>)
    fun onLoadingError(errorMessage: String)
    fun showErrorMessage(errorMessage: String)
    fun onLoading()
    fun onRefresh()
    fun onRefreshComplete()
}
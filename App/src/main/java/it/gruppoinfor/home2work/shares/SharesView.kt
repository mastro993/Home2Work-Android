package it.gruppoinfor.home2work.shares

import it.gruppoinfor.home2workapi.chat.Chat
import it.gruppoinfor.home2workapi.share.Share


interface SharesView {

    fun setItems(list: List<Share>)
    fun onLoadingError(errorMessage: String)
    fun onLoading()

}
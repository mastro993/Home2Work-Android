package it.gruppoinfor.home2work.chat


interface ChatView {
    fun setItems(list: List<Message>)
    fun onLoadingError(errorMessage: String)
    fun onLoading()
    fun onNewMessage(message: Message)
    fun onMessageSent()
    fun onMessageSentError(errorMessage: String)
}